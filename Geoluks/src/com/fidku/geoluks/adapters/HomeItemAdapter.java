 
package com.fidku.geoluks.adapters;

import java.util.List;

import com.fidku.geoluks.HomeActivity;
import com.fidku.geoluks.R;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.utils.DownloadImageAsync;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeItemAdapter extends ArrayAdapter<Products> {

	List<Products> data;
	HomeActivity context;
	int layoutResID;

	public HomeItemAdapter(HomeActivity context, int layoutResourceId,
			List<Products> data) {
		super(context, layoutResourceId, data);

		this.data = data;
		this.context = context;
		this.layoutResID = layoutResourceId; 
	}

	public void remove(int position) {
		data.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View row, ViewGroup parent) {

		NewsHolder holder = null;

		holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResID, parent, false);

			holder = new NewsHolder();
			/**
			 * Inicializando textViews
			 */
			holder.tv_product_price = (TextView) row
					.findViewById(R.id.product_price);
			holder.tv_product_title = (TextView) row
					.findViewById(R.id.product_title);
			holder.tv_product_description = (TextView) row
					.findViewById(R.id.product_description);
			holder.product_image = (ImageView) row
					.findViewById(R.id.product_image);
			/** 
			 * Inicializando botones
			 */
			holder.btn_compartir = (ImageButton) row
					.findViewById(R.id.btn_compartir);
			holder.btn_editar = (ImageButton) row.findViewById(R.id.btn_editar);
			holder.btn_undo = (ImageButton) row.findViewById(R.id.btn_undo);

		 
			row.setTag(holder);
		} else {
			holder = (NewsHolder) row.getTag();
		}

		final Products itemdata = data.get(position);
		holder.tv_product_description.setText(itemdata.getDescription());
		holder.tv_product_price.setText("$ "+Double.toString(itemdata.getPrice().getValue()));
		holder.tv_product_title.setText(itemdata.getTitle());

		holder.btn_undo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					context.setCkeckPrice(position, false);
			}
		});
		holder.btn_compartir.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "Revisa el producto "+itemdata.getTitle() +" su precio es $"+Double.toString(itemdata.getPrice().getValue()));
				sendIntent.setType("text/plain");
				context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.geoluks_app_name)));
			}
		});

		holder.btn_editar.setOnClickListener(new OnClickListener() {  
			@Override
			public void onClick(View v) {
				context.editarPrice(position);
			}
		});
		 

		new DownloadImageAsync(holder.product_image).execute(data.get(position)
				.getImageUrl());

		 

		return row;

	}

	static class NewsHolder {

		TextView tv_product_description;
		TextView tv_product_title;
		TextView tv_product_price;
		ImageView product_image;
		ImageButton btn_editar;
		ImageButton btn_compartir;
		ImageButton btn_undo;

	}

}
 