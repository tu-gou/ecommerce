package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    public class Orders implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private String userId;

    private String loginName;

    private String userAddress;

    private Float cost;

    private String serialnumber;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateDate;


}
