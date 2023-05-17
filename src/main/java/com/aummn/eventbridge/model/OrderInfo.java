/**
 * 
 */
package com.aummn.eventbridge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author James
 *
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderInfo implements Serializable {
	private String orderNo;
	private String item;
	private int quantity;
	private BigDecimal price;
	private String shippingInfo;
}
