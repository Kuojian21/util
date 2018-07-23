package com.test.facebook;

import java.util.List;
import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINode;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.APIRequest;
import com.facebook.ads.sdk.APIResponse;
import com.facebook.ads.sdk.Ad;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.AdCreative;
import com.facebook.ads.sdk.AdSet;
import com.facebook.ads.sdk.BatchRequest;
import com.facebook.ads.sdk.Campaign;
import com.google.common.base.Strings;

public class FacebookTest {

	public static void info(String campaignId, APIContext context) throws APIException {
		Map<String, Object> result = Maps.newHashMap();
		Campaign campaign = Campaign.fetchById("23842960320830146", context);
		// System.out.println(campaign);
		AdSet adset = campaign.getAdSets().requestAllFields().execute().get(0);
		// System.out.println(adset);
		Ad ad = adset.getAds().requestAllFields().execute().get(0);
		// System.out.println(ad);
		APINodeList<AdCreative> adCreatives = ad.getAdCreatives().requestAllFields().execute();
		// System.out.println(adCreatives);
		result.put("campaign", campaign);
		result.put("adset", adset);
		result.put("ad", ad);
		result.put("adCreatives", adCreatives);
		System.out.println(result);
	}

	public static void main(String[] args) throws APIException {
		if (args.length < 2) {
			return;
		}
		/*
		 * -DsocksProxyHost= -DsocksProxyPort=8088
		 */

		// System.setProperty("socksProxyHost", "127.0.0.1");
		// System.setProperty("socksProxyPort", "8088");

		AdCreative adCreative = new AdCreative();
		adCreative.setFieldId("");
		System.out.println(Strings.isNullOrEmpty(adCreative.getId()));
		APIContext context = new APIContext(args[0]);
		AdAccount adAccount = new AdAccount("1863152260418006", context);
		APINodeList<Campaign> campaigns = adAccount.getCampaigns().requestAllFields().execute();
		System.out.println(campaigns);

		String[] campaignIds = new String[] { "23842960320830146", "23842960216170146" };
		for(String campaignId : campaignIds) {
			info(campaignId,context);
		}
		
		// 23842960320830146
		APINodeList<AdSet> adsets = Campaign.fetchById("23842960320830146", context).getAdSets().requestAllFields()
				.execute();
		APINodeList<AdCreative> adcreatives = adsets.get(0).getAds().execute().get(0).getAdCreatives()
				.requestAllFields().execute();
		System.out.println(adcreatives);

		System.out.println(AdCreative.fetchById("23842948931830762", context));

		APIRequest<APINode> request = new APIRequest<>(context, "23842948931020762", "/", "GET");
		APIResponse res = request.execute();
		System.out.println(res);

		request = new APIRequest<>(context, "23843018011620094", "/", "GET");
		res = request.requestField("promoted_object").execute();
		System.out.println(res);
		request = new APIRequest<>(context, "279866052811123", "/thumbnails", "GET");
		res = request.execute();
		System.out.println(res);

		System.out.println(adAccount.getId());
		System.out.println(adAccount.getFieldAccountId());
		BatchRequest batch = new BatchRequest(context);
		for (int i = 0; i < 5; i++) {
			batch.addRequest(new AdAccount(args[1], context).createCampaign().setName(args[2] + i)
					.setObjective(Campaign.EnumObjective.VALUE_APP_INSTALLS)
					.setStatus(Campaign.EnumStatus.VALUE_ACTIVE));
		}
		List<Map<String, String>> adSpecs = Lists.newArrayList();
		List<APIResponse> response = batch.execute();
		adAccount.createAsyncAdRequestSet().setAdSpecs(adSpecs).addToBatch(batch).execute();
		System.out.println(response);

		// BatchRequest request = new BatchRequest(context);
		// for (int i = 1; i < args.length; i++) {
		// request.addRequest(new Campaign(args[i], context).get().requestAllFields());
		// }
		// List<APIResponse> list = request.execute();
		// System.out.println(list);
		// APINodeList<AdSet> adSets = new Campaign(args[0],
		// context).getAdSets().requestAllFields().execute();
		// System.out.println(adSets);
		// APINodeList<Ad> ads = new Campaign(args[0],
		// context).getAds().requestStatusField().execute();
		// System.out.println(ads);
	}

}
