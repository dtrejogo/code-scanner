package com.fidku.geoluks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle; 
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListProductCategoryActivity extends BaseActivity {
	protected static final String EXTRA_CATEGORY_ID = null;
	protected ArrayList<Category> listaCategorias;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_product_category_activity);
		setTitle("Categorias"); 

		final ListView listview = (ListView) findViewById(R.id.lv_categories);
		
		ApiConnection.getCategories(new HttpApiAdapter() {
			
			@Override
			public void onSuccess(boolean status, String message, Object response) {
				listaCategorias = new ArrayList<Category>();
				JSONArray res = (JSONArray) response;
				String[] categories = new String[res.length()];
				for (int i = 0; i < categories.length; i++) {
					try {
						listaCategorias.add(new Category(res.getJSONObject(i)
								.getJSONObject("Categories").getInt("id"), res.getJSONObject(i)
								.getJSONObject("Categories").getString(
								"name")));
						categories[i] = listaCategorias.get(i).getName();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				
				ArrayAdapter<String> categorias = new ArrayAdapter<String>(
						ListProductCategoryActivity.this,
						android.R.layout.simple_list_item_1, categories);

				listview.setAdapter(categorias);
				categorias.notifyDataSetChanged();
				
			}
		});
		
		 

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				Intent i= new Intent();
				 
				Toast.makeText(getApplicationContext(), listaCategorias.get(position).getName(), Toast.LENGTH_LONG).show();
				  

			}

		});

	} 

}
