package com.kavin.solr.services;


import com.kavin.solr.bean.ResultModel;

/**
 *  商品服务
 *  @author kavin
 */
public interface ProductService {

	public ResultModel getProducts(String queryString, String catalogName,
								   String price, String sort, Integer page) throws Exception;
}
