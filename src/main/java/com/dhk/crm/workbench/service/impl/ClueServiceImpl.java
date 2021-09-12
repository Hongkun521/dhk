package com.dhk.crm.workbench.service.impl;

import com.dhk.crm.utils.DateTimeUtil;
import com.dhk.crm.utils.UUIDUtil;
import com.dhk.crm.workbench.dao.*;
import com.dhk.crm.workbench.domain.*;
import com.dhk.crm.workbench.service.ClueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ClueServiceImpl implements ClueService {
    //线索相关表
    @Resource
    private ClueDao clueDao;
    @Resource
    private ClueActivityRelationDao clueActivityRelationDao;
    @Resource
    private ClueRemarkDao clueRemarkDao;
    //客户相关表
    @Resource
    private CustomerDao customerDao;
    @Resource
    private CustomerRemarkDao customerRemarkDao;
    //联系人相关表
    @Resource
    private ContactsDao contactsDao;
    @Resource
    private ContactsRemarkDao contactsRemarkDao;
    @Resource
    private ContactsActivityRelationDao contactsActivityRelationDao;
    //交易相关表
    @Resource
    private TranDao tranDao;
    @Resource
    private TranHistoryDao tranHistoryDao;

    @Override
    public boolean save(Clue clue) {
        boolean flag=true;

        int count=clueDao.save(clue);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> pageList(Map<String, Object> map) {
        Integer total=clueDao.getTotalByCondition(map);
        List<Clue> clueList=clueDao.pageList(map);
        Map<String,Object> map1=new HashMap<>();
        map1.put("total",total);
        map1.put("clueList",clueList);
        return map1;
    }

    @Override
    public Clue detail(String id) {
        Clue clue=clueDao.detail(id);
        return clue;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag=true;

        int count=clueActivityRelationDao.unbund(id);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean bund(String clueId, String[] activityId) {
        boolean flag=true;

        ClueActivityRelation car=new ClueActivityRelation();
        //但是这样存在一个频繁访问dao层的问题，建议后期优化为List<ClueActivityRelation>数组传输到dao层用foreach插入数据
        for(String aid:activityId) {

            // 取得每一个aid和cid做关联
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(aid);

            // 添加关联关系表中的记录
            int count = clueActivityRelationDao.bund(car);
            if (count != 1) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag=true;

        String createTime= DateTimeUtil.getSysTime();

        // (1)通过线索id获取线索对象(线索对象当中封装了线索信息)
        Clue clue=clueDao.getById(clueId);

        // (2)通过线索对象提取客户信息,当客户不存在的时候,新建客户(根据公司的名称精确匹配,判断客户是否存在!)
        String company=clue.getCompany();
        Customer cus=customerDao.getCustomerByName(company);
        // 如果cus为null,说明以前没有这个客户,需要新建一个
        if(cus==null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(clue.getAddress());
            cus.setPhone(clue.getPhone());
            cus.setOwner(clue.getOwner());
            cus.setNextContactTime(clue.getNextContactTime());
            cus.setWebsite(clue.getWebsite());
            cus.setDescription(clue.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(clue.getContactSummary());
            cus.setName(company);
            // 添加客户
            int count1 = customerDao.save(cus);

            if(count1!=1){
                flag=false;
            }

        }

        // -------------------------------------------------------------------------------
        // 经过第二步处理后,客户的信息我们已经拥有了,将来在处理其他表的时候,如果要使用到客户的id
        // 直接使用cus.getId();
        // -------------------------------------------------------------------------------

        // (3)通过线索对象提取联系人信息,保存联系人
        Contacts contacts=new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(cus.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());

        int count2=contactsDao.save(contacts);
        if(count2!=1){
            flag=false;
        }

        // -------------------------------------------------------------------------------
        // 经过第三步处理后,联系人的信息我们已经拥有了,将来在处理其他表的时候,如果要使用到联系人的id
        // 直接使用contacts.getId();
        // -------------------------------------------------------------------------------

        // (4)线索备注转换到客户备注以及联系人备注
        // 查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList=clueRemarkDao.getListByClueId(clueId);
        //取出每一条线索备注
        for(ClueRemark clueRemark:clueRemarkList){

            // 取出每一条线索的备注信息(主要转换到客户备注和联系人备注的就是这个备注信息)
            String noteContent = clueRemark.getNoteContent();

            // 创建客户备注对象,添加客户备注信息
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){
                flag = false;
            }

            // 创建联系人备注对象,添加联系人备注信息
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }

        }

        // (5)"线索和市场活动"的关系转换到"联系人和市场活动"的关系
        // 查询出与该条线索关联的市场活动,查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelationList=clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            //从每一条遍历出来的记录中去除关联市场活动的id
            String activityId=clueActivityRelation.getActivityId();

            //创建  联系人与市场活动的关联关系对象 让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation=new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            int count5=contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                flag=false;
            }
        }

        //(6)如果有创建交易需求，创建一条交易
        if(t!=null){
            /**
             * t对象在controller里面已经封装好的信息如下:
             *  id,money,name,expectedDate,stage,activityId,createBy,createTime
             *
             *  接下来可以通过第一步生成的c对象,取出一些信息,继续完善对t对象的封装
             */
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setNextContactTime(clue.getNextContactTime());
            t.setCustomerId(cus.getId());
            t.setContactSummary(clue.getContactSummary());
            t.setContactsId(contacts.getId());
            //添加交易
            int count6=tranDao.save(t);
            if(count6!=1){
                flag=false;
            }
            // (7)如果创建了交易,则创建一条该交易下交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            // 添加交易历史
            int count7 = tranHistoryDao.save(th);
            if(count7 != 1){
                flag = false;
            }

        }

        //(8)删除线索备注
        for (ClueRemark clueRemark:clueRemarkList){
            int count8=clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                flag=false;
            }
        }

        //(9)删除线索和市场活动关系
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            int count9=clueActivityRelationDao.delete(clueActivityRelation);
            if(count9!=1){
                flag=false;
            }
        }

        //(10)删除线索
        int count10=clueDao.delete(clueId);
        if(count10!=1){
            flag=false;
        }


        return flag;


    }


}
