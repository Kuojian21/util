package com.test.facebook;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.curator.shaded.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINode;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.APIRequest;
import com.facebook.ads.sdk.APIRequest.IAsyncRequestExecutor;
import com.facebook.ads.sdk.APIResponse;
import com.facebook.ads.sdk.Ad;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.AdAccount.APIRequestCreateCampaign;
import com.facebook.ads.sdk.AdCreative;
import com.facebook.ads.sdk.AdSet;
import com.facebook.ads.sdk.BatchRequest;
import com.facebook.ads.sdk.Campaign;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;

public class FacebookTest {

	public static void info(APIContext context) throws APIException {
		Map<String, Object> result = Maps.newHashMap();
		Campaign campaign = Campaign.fetchById("23842915793690575", context);
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
		System.out.println(JSON.toJSONString(result));
	}

	public static void asyncBatch(AdAccount adAccount)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, APIException, InterruptedException, ExecutionException {
		List<Object> batch = Lists.newArrayList();
		Method method = APIRequest.class.getDeclaredMethod("getBatchModeRequestInfo");
		method.setAccessible(true);
		for (int i = 0; i < 5; i++) {
			APIRequestCreateCampaign request = adAccount.createCampaign().setName("test" + i)
					.setObjective(Campaign.EnumObjective.VALUE_APP_INSTALLS)
					.setStatus(Campaign.EnumStatus.VALUE_ACTIVE);
			BatchRequest.BatchModeRequestInfo info = (BatchRequest.BatchModeRequestInfo) method.invoke(request,
					new Object[] {});
			JsonObject batchElement = new JsonObject();
			batchElement.addProperty("method", info.method);
			batchElement.addProperty("relative_url", info.relativeUrl);
			batchElement.addProperty("name", "RequestCreative" + i);
			batchElement.addProperty("body", info.body);
			batch.add(batchElement);
		}
		APIRequest.changeAsyncRequestExecutor(new com.facebook.ads.sdk.APIRequest.DefaultAsyncRequestExecutor());
		ListenableFuture<APIResponse> future = adAccount.createAsyncBatchRequest().setAdbatch(batch).executeAsyncBase();
		System.out.println(future.get());
	}

	public static void main(String[] args)
			throws APIException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InterruptedException, ExecutionException {
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
		System.out.println(AdSet.fetchById("23842915793710575", context));
		System.out.println(AdSet.fetchById("23842915827060575", context));
		info(context);
		System.out.println(new APIRequest<APINode>(context, "304490227015372", "/", "GET").execute());
		Campaign campaign = Campaign.fetchById("23842903817110320", context);
		System.out.println(campaign);

		AdAccount adAccount = new AdAccount("1863152260418006", context);

		
		asyncBatch(adAccount);

		adAccount.getCampaigns();

		System.out.println(AdAccount.fetchById("1038971129618045", context));
		APIRequest<APINode> request = new APIRequest<APINode>(context, "me", "/adaccounts", "GET");
		System.out.println(request.execute());

		asyncBatch(adAccount);

		APINodeList<Campaign> campaigns = adAccount.getCampaigns().requestAllFields().execute();
		System.out.println(campaigns);

		String[] campaignIds = new String[] { "23842960320830146", "23842960216170146" };
		for (String campaignId : campaignIds) {
//			info(campaignId, context);
		}

		// 23842960320830146
		APINodeList<AdSet> adsets = Campaign.fetchById("23842960320830146", context).getAdSets().requestAllFields()
				.execute();
		APINodeList<AdCreative> adcreatives = adsets.get(0).getAds().execute().get(0).getAdCreatives()
				.requestAllFields().execute();
		System.out.println(adcreatives);

		System.out.println(AdCreative.fetchById("23842948931830762", context));

		request = new APIRequest<>(context, "23842948931020762", "/", "GET");
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

		adAccount.createAsyncBatchRequest().addToBatch(batch);

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
