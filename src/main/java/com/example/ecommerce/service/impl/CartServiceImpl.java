package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.mapper.*;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.service.CartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.utils.AESUtil;
import com.example.ecommerce.utils.RsaUtils;
import com.example.ecommerce.utils.SHA1;
import com.example.ecommerce.vo.CartVO;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Service
@Slf4j
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private RsaMapper rsaMapper;
    @Override
    @Transactional
    public Boolean add(Cart cart) {
        //添加购物车
        int insert = this.cartMapper.insert(cart);
        if (insert != 1) {
            throw new EcommerceException(ResponseEnum.CART_ADD_ERROR);
        }
        //商品减库存
        Integer stock=this.productMapper.getStockById(cart.getProductId());
        if (stock == null) {
            log.info("【添加购物车】商品不存在");
            throw new EcommerceException(ResponseEnum.PRODUCT_NOT_EXISTS);
        }
        if (stock == 0) {
            log.info("【添加购物车】库存不足");
            throw new EcommerceException(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        Integer newStock=stock- cart.getQuantity();
        if (newStock < 0) {
            log.info("【添加购物车】库存不足");
            throw new EcommerceException(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        this.productMapper.updateStockById(cart.getProductId(),newStock);
        return true;
    }

    @Override
    public List<CartVO> findVOListByUserId(Integer userId) {
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Cart> cartList=this.cartMapper.selectList(queryWrapper);
        List<CartVO> cartVOList=new ArrayList<>();
        for (Cart cart : cartList) {
            Product product=this.productMapper.selectById(cart.getProductId());
            CartVO cartVO=new CartVO();
            BeanUtils.copyProperties(cart,cartVO);
            BeanUtils.copyProperties(product,cartVO);
            cartVOList.add(cartVO);
        }
        return cartVOList;
    }

    @Override
    @Transactional
    public Boolean update(Integer id, Integer quantity, Float cost) {
        //更新库存
        //int oldQuantity=this.cartMapper.getQuantityById(id);
        Cart cart = this.cartMapper.selectById(id);
        Integer oldQuantity=cart.getQuantity();
        //判断quantity的变化
        if(quantity.equals(oldQuantity)){
            log.info("【更新购物车】参数错误");
            throw new EcommerceException(ResponseEnum.CART_UPDATE_PARAMETER_ERROR);
        }
        int result =quantity-oldQuantity;
        //查询商品库存
        Integer stock=this.productMapper.getStockById(cart.getProductId());
        Integer newStock=stock-result;
        if (newStock < 0) {

            log.info("【更新购物车】商品库存错误");
            throw new EcommerceException(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        Integer integer = this.productMapper.updateStockById(cart.getProductId(), newStock);
        if (integer != 1) {
            log.info("【更新购物车】更新商品库存失败");
            throw new EcommerceException(ResponseEnum.CART_UPDATE_STOCK_ERROR);
        }
        //更新数据
        int update = this.cartMapper.update(id, quantity, cost);
        if(update!=1){
            log.info("【更新购物车】更新失败");
            throw new EcommerceException(ResponseEnum.CART_UPDATE_ERROR);
        }


        return true;
    }

    @Override
    @Transactional
    public Boolean delete(Integer id) {
        //更新商品库存
        Cart cart = this.cartMapper.selectById(id);
        Integer stock = this.productMapper.getStockById(cart.getProductId());
        Integer newStock=stock+cart.getQuantity();
        Integer integer=this.productMapper.updateStockById(cart.getProductId(),newStock);
        if (integer != 1) {
            log.info("【更新购物车】更新商品库存失败");
            throw new EcommerceException(ResponseEnum.CART_UPDATE_STOCK_ERROR);
        }
        //删除购物车记录
        int i = this.cartMapper.deleteById(id);
        if (i != 1) {
            log.info("【删除购物车】删除失败");
            throw new EcommerceException(ResponseEnum.CART_REMOVE_ERROR);
        }

        return true;
    }

    @Override
    @Transactional
    public Orders commit(String userAddress,String address,String remark,String HPI,String signHOP, String secretKey,User user) throws Exception {

        System.out.println(signHOP);
        //验证签名
//        QueryWrapper<Rsa> queryWrapper2=new QueryWrapper<>();
//        queryWrapper2.eq("user",user.getUserName());
//        Rsa rsa=new Rsa();
//        rsa=this.rsaMapper.selectOne(queryWrapper2);
        String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMBgF0F/y5TG5/pAnszschMKzcVctZY4XWzduZePRzILfUrOdhDsQMOpaRAgWmLypNfkz51iusgj+oUT2eKRm5/inQUI+aurNUHxfzr4Rf3znB9L/2sk+wfHbSUxH+FAPzapBug+n0YDM/xATi5X1+4XzOd3o3Prv1l5eE4wwI35AgMBAAECgYAGmq3ltN9OcNspJVOGTbY3VuM8S6vct7vmV0DONhAfxNL08NxPBatOBEh78h5kP/f+hOeHO8rVgN2yeJ1d6LmGzvPQeylo0WXzdNKLEw78ALhc0cD310+H3bM+ljedYV79G6rRb2dd2Nh/27e5jw2zN0NIQHym/zwdkiw8ZZ4c8QJBAPSMvZQ7jvnqHoL4/MCojGldCLyWUHbXOMoXeaxoyZcaw8AY1PqPHcFBudtT0new/JtudGSKI6WB/ad2zj+XrdECQQDJYfgt/t8r5Mq7/0dEBmcBhY8EED4FaaM95QFb0Lm0gdqQTPIAXrJWwgGDRxpuqmsq/C5Yyc8K7l83HFvuip+pAkAuE4Z0s3QQ6metTbRxqFAsWfcXcUrf2VU93oZyUZwJ+GUBgKxAOU7l5rhZ9sUlABfYQUt01gI5YPCl6OCbYrzBAkEAvG5JFHCpTZ70+9evTT1YYJoh6cFw5wujTSTckpbJTNc8NU1qG2KAKKG7XKTJXMdlI4F3tGiQrD/DJAQLGTbokQJBAJGfzA7+qGQy0rZPyB0qar9TYrOKlCZNs42Y8T+azrF1aD613aYGrHLU8j35rcY/B0S2umWDG3Y5W148ylWx9+I=";//填写已创建好的商家私钥
        if(!(RsaUtils.decryptByPrivateKey(privateKey,signHOP)).equals(SHA1.sha1(SHA1.sha1(userAddress+address+remark)+HPI))){
            log.info("【支付信息签名有误");
            throw new EcommerceException(ResponseEnum.PAYMENT_SIGN_ERROR);
        }

        //获取对称加密密钥
        String key= RsaUtils.decryptByPrivateKey(privateKey,secretKey);

        userAddress= AESUtil.decrypt(userAddress,key);
        address=AESUtil.decrypt(address,key);
        remark=AESUtil.decrypt(remark,key);

        //处理地址
        if(!userAddress.equals("newAddress")){//未选择新地址
            address=userAddress;
        }else{
//            int i = this.userAddressMapper.setDefault();
//            if (i == 0) {
//                log.info("【确认订单】修改默认地址失败");
//                throw new EcommerceException(ResponseEnum.USER_ADDRESS_SET_DEFAULT_ERROR);
//            }
            //将新地址存入数据库
            UserAddress userAddress1=new UserAddress();
            userAddress1.setIsdefault(1);
            userAddress1.setUserId(user.getId());
            userAddress1.setRemark(remark);
            userAddress1.setAddress(address);
            int insert=this.userAddressMapper.insert(userAddress1);
            if (insert == 0) {
                log.info("【确认订单】添加新地址失败");
                throw new EcommerceException(ResponseEnum.USER_ADDRESS_ADD_ERROR);
            }

        }

        //创建订单主表
        Orders orders=new Orders();
        orders.setUserId(user.getId().toString());
        orders.setLoginName(user.getLoginName());
        orders.setUserAddress(address);
        orders.setCost(this.cartMapper.getCostByUserId(user.getId()));
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String serialNumber=formatter.format(date).toString();
        orders.setSerialnumber(serialNumber);
        orders.setUpdateDate(LocalDateTime.now());

        //订单主表存储加密
        Orders orders1=new Orders();
        orders1.setUserId(AESUtil.encrypt(user.getId().toString(),"uUXsN6okXYqsh0BB"));
        orders1.setLoginName(AESUtil.encrypt(user.getLoginName(),"uUXsN6okXYqsh0BB"));
        orders1.setUserAddress(AESUtil.encrypt(address,"uUXsN6okXYqsh0BB"));
        orders1.setCost(this.cartMapper.getCostByUserId(user.getId()));
        orders1.setSerialnumber(AESUtil.encrypt(serialNumber,"uUXsN6okXYqsh0BB"));
        orders1.setUpdateDate(LocalDateTime.now());
        int insert = this.ordersMapper.insert(orders1);
        if (insert !=1) {
            log.info("【确认订单】创建订单主表失败");
            throw new EcommerceException(ResponseEnum.ORDERS_CREATE_ERROR);
        }

        //创建订单从表
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        List<Cart> carts=this.cartMapper.selectList(queryWrapper);
        for (Cart cart : carts) {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(1);
            int insert1 = this.orderDetailMapper.insert(orderDetail);
                if (insert1 == 0) {
                    log.info("【确认订单】创建订单详情失败");
                    throw new EcommerceException(ResponseEnum.ORDER_DETAIL_CREATE_ERROR);
            }
        }
        //清空当前用户购物车
        QueryWrapper<Cart> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("user_id",user.getId());
        int delete=this.cartMapper.delete(queryWrapper1);
//        if (delete != 1) {
//                log.info("【确认订单】清空购物车失败");
//            throw new EcommerceException(ResponseEnum.CART_REMOVE_ERROR);
//        }

        return orders;
    }
}
