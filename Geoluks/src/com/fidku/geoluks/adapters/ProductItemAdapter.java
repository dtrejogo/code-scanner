package com.fidku.geoluks.adapters;

import java.util.List;

import com.fidku.geoluks.HomeActivity;
import com.fidku.geoluks.ProductActivity;
import com.fidku.geoluks.R;
import com.fidku.geoluks.beans.Place;
import com.fidku.geoluks.beans.Price;
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

public class ProductItemAdapter extends ArrayAdapter<Price> {

	List<Price> data;
	ProductActivity context;
	int layoutResID;

	public ProductItemAdapter(ProductActivity productActivity,
			int layoutResourceId, List<Price> data) {
		super(productActivity, layoutResourceId, data);

		this.data = data;
		this.context = productActivity;
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
			holder.address_places = (TextView) row
					.findViewById(R.id.address_places);
			holder.price_value = (TextView) row
					.findViewById(R.id.price_value); 
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
		
		if(holder==null){ 
			return row;
		} 
		final Price itemdata = data.get(position); 
		holder.address_places.setText(itemdata.getPlace().getName()); 
		holder.price_value.setText("$ "+Double.toString(itemdata.getValue()));
		holder.btn_compartir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				  
				   /*Intent sendIntent = new Intent();
				  sendIntent.setAction(Intent.ACTION_SEND);
				   sendIntent.putExtra( Intent.EXTRA_TEXT, "Revisa el producto "
				   + itemdata.get + " su precio es $" +
				   Double.toString(itemdata.getPrice() .getValue()));
				   sendIntent.setType("text/plain");
				   context.startActivity(Intent.createChooser(sendIntent,
				   context.getResources().getText(R.string.geoluks_app_name)));*/
				   
			}
		});

		holder.btn_editar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return row;

	}

	static class NewsHolder {

		TextView address_places;
		TextView upload_for;
		TextView price_value;
		ImageButton btn_editar;
		ImageButton btn_compartir;
		ImageButton btn_undo;

	}

}
