package com.leyou.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.pojo.UserInfo;
import com.lx.leyou.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper detailMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Long createOrder(Order order) {
        // 生成orderId
        long orderId = idWorker.nextId();
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        // 初始化数据
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setUserId(user.getId());
        // 保存数据
        this.orderMapper.insert(order);

        // 保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(1);// 初始状态为未付款

        this.statusMapper.insert(orderStatus);

        // 订单详情中添加orderId
        order.getOrderDetails().forEach(od -> od.setOrderId(orderId));
        // 保存订单详情,使用批量插入功能
        List<OrderDetail> orderDetails = order.getOrderDetails();
        //this.detailMapper.insertList(order.getOrderDetails());
        orderDetails.forEach(orderDetail -> {
            this.detailMapper.insert(orderDetail);
        });

        logger.debug("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());

        return orderId;
    }

    public Order queryById(Long id) {
        // 查询订单
        Order order = this.orderMapper.selectById(id);

        // 查询订单详情
        QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
        qw.eq("id", id);
        List<OrderDetail> details = this.detailMapper.selectList(qw);
        order.setOrderDetails(details);

        // 查询订单状态
        OrderStatus status = this.statusMapper.selectById(order.getOrderId());
        order.setStatus(status.getStatus());
        return order;
    }

    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        try {
            QueryWrapper<OrderStatus> qw = new QueryWrapper<>();
            QueryWrapper<Order> orderQW = new QueryWrapper<>();
            IPage<Order> orderPage = new Page<>();
            // 分页
            orderPage.setCurrent(page);
            orderPage.setSize(rows);

            // 获取登录用户
            UserInfo user = LoginInterceptor.getLoginUser();
            qw.eq("status", status);
            //将order集合转换成id集合
            List<Long> orderIds = this.statusMapper.selectList(qw).stream().map(OrderStatus::getOrderId).collect(Collectors.toList());
            orderQW.in("order_id", orderIds);
            // 创建查询条件
            IPage<Order> iPage = orderMapper.selectPage(orderPage, orderQW);

            long pages = iPage.getPages();
            int size = (int) iPage.getSize();
            List<Order> records = iPage.getRecords();

            return new PageResult<>(pages, size, records);
        } catch (Exception e) {
            logger.error("查询订单出错", e);
            return null;
        }
    }

    @Transactional
    public Boolean updateStatus(Long id, Integer status) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(id);
        orderStatus.setStatus(status);
        // 根据状态判断要修改的时间
        switch (status) {
            case 2:
                orderStatus.setPaymentTime(new Date());// 付款
                break;
            case 3:
                orderStatus.setConsignTime(new Date());// 发货
                break;
            case 4:
                orderStatus.setEndTime(new Date());// 确认收获，订单结束
                break;
            case 5:
                orderStatus.setCloseTime(new Date());// 交易失败，订单关闭
                break;
            case 6:
                orderStatus.setCommentTime(new Date());// 评价时间
                break;
            default:
                return null;
        }
        int count = this.statusMapper.updateById(orderStatus);
        return count == 1;
    }

}
