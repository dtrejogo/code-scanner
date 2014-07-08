package com.fidku.geoluks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.ListCategory;
import com.fidku.geoluks.beans.User;
import com.fidku.geoluks.parameters.App;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddListFragment extends Fragment {

	private View rootView;
	private EditText nameView;
	private List<ListCategory> categoryList;
	private Spinner categoryView;
	private Context context;
	private View mStatusView;
	private TextView processingMessage;
	private View formView;

	public AddListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.fragment_add_list,
				container, false);

		this.context = getActivity();
		prepareViewItems();
		populateCategories();

		return this.rootView;
	}

	private void prepareViewItems() {

		this.rootView.findViewById(R.id.add_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addList();
					}

				});

		this.categoryView = (Spinner) AddListFragment.this.rootView
				.findViewById(R.id.categories);

		this.nameView = (EditText) this.rootView.findViewById(R.id.name);

		this.mStatusView = this.rootView.findViewById(R.id.login_status);
		this.processingMessage = (TextView) this.rootView
				.findViewById(R.id.status_message);
		this.formView = this.rootView.findViewById(R.id.form);

	}

	/**
	 * try to save an offer
	 */
	public void addList() {

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		nameView.setError(null);

		// Store values at the time of the login attempt.
		String name = nameView.getText().toString().trim();

		ListCategory selectedCategory = this.categoryList
				.get(this.categoryView.getSelectedItemPosition());

		if (selectedCategory == null) {
			focusView = this.categoryView;
			cancel = true;
		}

		// Check for a valid title.
		if (TextUtils.isEmpty(name)) {
			nameView.setError(getString(R.string.error_field_required));
			focusView = nameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			processingMessage.setText(R.string.add_offer_processing);
			showProgress(true);

			SharedPreferences getShared = this.context
					.getSharedPreferences(App.KEY_SESSION_NAME,
							Context.MODE_PRIVATE);

			com.fidku.geoluks.beans.List list = new com.fidku.geoluks.beans.List();
			list.setName(name);
			list.setCategory(selectedCategory);
			User user = new User();
			user.setId(getShared.getString(App.KEY_USER_UID, null));
			list.setUser(user);

			ApiConnection.saveList(list, new HttpApiAdapter() {

				@Override
				public void onSuccess(boolean status, String message,
						Object response) {
					String successMessage;
					if (status) {
						successMessage = getResources().getString(
								R.string.list_added_success);
					} else {
						successMessage = message;
					}

					Toast.makeText(context, successMessage,
							Toast.LENGTH_LONG).show();
					
					startActivity(new Intent(context.getApplicationContext(),
							HomeActivity.class));
					
					getActivity().finish();

				}
			});

		}
	}

	private void populateCategories() {

		ApiConnection.getListCategory(new HttpApiAdapter() {

			@Override
			public void onSuccess(boolean status, String message,
					Object response) {
				if (status) {
					try {
						JSONArray results = (JSONArray) response;
						AddListFragment.this.categoryList = new ArrayList<ListCategory>();
						String[] categoryArray = new String[results
								.length()];

						for (int i = 0; i < results.length(); i++) {
							JSONObject obj = results.getJSONObject(i);
							JSONObject category = obj
									.getJSONObject("ListCategory");

							ListCategory categoryObj = new ListCategory();
							categoryObj.setId(category.getString("id"));
							categoryObj.setName(category.getString("name"));
							categoryList.add(categoryObj);
							categoryArray[i] = categoryObj.getName();
						}

						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								AddListFragment.this.getActivity(),
								R.layout.spinner_item, categoryArray);
						adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						AddListFragment.this.categoryView
								.setAdapter(adapter);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});

	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which
		// allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			formView.setVisibility(View.VISIBLE);
			formView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							formView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply
			// show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			formView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
