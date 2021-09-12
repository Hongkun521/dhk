package com.dhk.crm.workbench.service.impl;

import com.dhk.crm.utils.DateTimeUtil;
import com.dhk.crm.utils.UUIDUtil;
import com.dhk.crm.workbench.dao.CustomerDao;
import com.dhk.crm.workbench.dao.TranDao;
import com.dhk.crm.workbench.dao.TranHistoryDao;
import com.dhk.crm.workbench.domain.Clue;
import com.dhk.crm.workbench.domain.Customer;
import com.dhk.crm.workbench.domain.Tran;
import com.dhk.crm.workbench.domain.TranHistory;
import com.dhk.crm.workbench.service.TranService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranServiceImpl implements TranService {
    @Resource
    private TranDao tranDao;
    @Resource
    private TranHistoryDao tranHistoryDao;
    @Resource
    private CustomerDao customerDao;

    @Override
    public boolean save(Tran tran, String customerName) {

        /*
            交易添加业务:
                在做添加之前，参数t里面少了一项信息，就是客户的主键，customerId

                所以要先处理客户相关的请求
                （1）判断customerName,根据客户名称在客户表进行精确查询
                    如果有这个客户，则取出这个客户的id，封装到t对象中
                    如果没有这个客户，则在客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中
                （2）经过以上操作后，t中的信息就全了，需要执行添加交易操作

                （3）添加交易完毕后，需要创建一个交易历史
         */

        boolean flag=true;

        Customer cus = customerDao.getCustomerByName(customerName);
        if(cus==null){
            //如果cus为空，需要创建用户
            cus=new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setCreateBy(tran.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(tran.getContactSummary());
            cus.setNextContactTime(tran.getNextContactTime());
            cus.setOwner(tran.getOwner());
            //添加客户
            int count1=customerDao.save(cus);
            if(count1!=1){
                flag=false;
            }
        }
        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前我们没有新增的客户，总之客户已经有了，客户的id就有了
        //将客户的id封装到tran对象中
        tran.setCustomerId(cus.getId());


        tran.setId(UUIDUtil.getUUID());

        //添加交易
        int count2=tranDao.save(tran);
        if(count2!=1){
            flag=false;
        }

        //添加交易历史
        TranHistory tranHistory=new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setCreateBy(tran.getCreateBy());

        int count3=tranHistoryDao.save(tranHistory);
        if(count3!=1){
            flag=false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t=tranDao.detail(id);
        return t;
    }

    @Override
    public Map<String, Object> pageList(Map<String, Object> map) {
        Integer total=tranDao.getTotalByCondition(map);
        List<Tran> tranList=tranDao.pageList(map);
        Map<String,Object> map1=new HashMap<>();
        map1.put("total",total);
        map1.put("tranList",tranList);
        return map1;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> thList=tranHistoryDao.getHistoryListByTranId(tranId);
        return thList;
    }

    @Override
    public boolean changeStage(Tran tran) {
        boolean flag=true;
        //改变交易阶段
        int count1=tranDao.changeStage(tran);
        if(count1!=1){
            flag=false;
        }


        //交易阶段改变后，生成一条交易历史
        TranHistory th=new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(tran.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(tran.getExpectedDate());
        th.setMoney(tran.getMoney());
        th.setTranId(tran.getId());
        th.setStage(tran.getStage());
        //添加交易历史
        int count2=tranHistoryDao.save(th);
        if (count2!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得total
        int total=tranDao.getTotal();
        //取得dataList
        List<Map<String,Object>> dataList=tranDao.getCharts();

        //取得total和dataList保存到map中
        Map<String,Object> map=new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
