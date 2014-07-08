package com.fidku.geoluks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.adapters.ProductItemAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Price;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.utils.DownloadImageAsync;
import com.fidku.geoluks.utils.Image;
import com.fortysevendeg.swipelistview.SwipeListView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductActivity extends BaseActivity {

	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String ParamProduct = "product";
	protected static final String ParamPriceValue = "price_value";
	protected static final String ParamPriceId = "price_id";
	protected static final String ParamAction = "action";
	private TextView tv_title;
	private ImageView iv_img_producr;
	private Products product;
	private SwipeListView swipelistview;
	private ArrayList<Price> itemData;
	private ProductItemAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_view);
		setTitle(getResources().getString(R.string.product_activity_title));
		iv_img_producr = (ImageView) findViewById(R.id.product_image);

		tv_title = (TextView) findViewById(R.id.tv_product_title);
		if (getIntent().getStringExtra(ParamProduct) != null) {
			try {
				product = new Products(getIntent().getStringExtra(ParamProduct));
				tv_title.setText(product.getTitle());
				new DownloadImageAsync(iv_img_producr).execute(product
						.getImageUrl());

				swipelistview = (SwipeListView) findViewById(R.id.product_lv_price);
				itemData = new ArrayList<Price>();
				adapter = new ProductItemAdapter(this, R.layout.product_places,
						itemData);

				swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);  
				 
				swipelistview
						.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); // there
			 
				swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
				swipelistview.setOffsetLeft(Image.convertDpToPixel(this, 190f));  
				swipelistview.setAnimationTime(200);   
				swipelistview.setSwipeOpenOnLongPress(true);  

				swipelistview.setAdapter(adapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		ApiConnection.getProductPlaces(product.getId() + "",
				new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						System.out.println("CARGADA LISTA:" + message);
						JSONArray res = (JSONArray) response;
						for (int i = 0; i < res.length(); i++) {
							try {
								System.out.println("Contenido index "
										+ i
										+ ":"
										+ res.getJSONObject(i).getString(
												"Price"));
								Price price = new Price(res.getJSONObject(i)
										.getString("Price"));
								price.setPlace(res.getJSONObject(i).getString(
										"Place"));
								itemData.add(price);
								System.out.println(price);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						adapter.notifyDataSetChanged();

					}
				});
	}
}
