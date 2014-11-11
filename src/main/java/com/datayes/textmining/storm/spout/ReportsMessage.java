package com.datayes.textmining.storm.spout;
/**   
* @Description: TODO
* @author weifu.du
* @date Sep 16, 2013 
* @version V1.0   
*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gson.Gson;

import clojure.string__init;

/**
 * @author weifu
 *
 */

class ReferItem {
	String how;
	String tableName;
	Map<String, String> key;
	
	public ReferItem() {
		key = new HashMap<String, String>();
	}
	
}

public class ReportsMessage {
	String storageType;
	String databaseAddress;
	List<ReferItem> refer;
	
	public ReportsMessage() {
		refer = new ArrayList<ReferItem>();
	}
	
	public static void main(String[] args) {
		//DEBUG
//		Gson gson = new Gson();
//		NewsMessage message = new NewsMessage();
//		message.storageType = "mysql";
//		message.databaseAddress = "10.0.0.1";
//		ReferItem item = new ReferItem();
//		item.tableName = "news_main";
//		item.how = "add";
//		item.key.put("news_id", "0001");
//		item.key.put("group_id", "0002");
//		message.refer.add(item);
//		String jsonStr = gson.toJson(message);
//		System.out.println(jsonStr);
//		
//		NewsMessage message2 = gson.fromJson(jsonStr, NewsMessage.class);
//		System.out.println(message2.refer.get(0).key.get("news_id"));
	}
}



