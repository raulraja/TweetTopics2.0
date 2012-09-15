package com.javielinux.tweettopics2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.SaveFirstTweetsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SaveFirstTweetsResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.fragmentadapter.SearchFragmentAdapter;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import com.viewpagerindicator.TabPageIndicator;

public class SearchActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    private static final int SAVE_ID = Menu.FIRST;
    private static final int SAVE_LAUNCH_ID = Menu.FIRST + 1;

    protected ProgressDialog progressDialog;

    private ViewPager pager;
    private SearchFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;

    private ThemeManager themeManager;
    private EntitySearch search_entity = null;
    private LinearLayout searchRoot;
    private RelativeLayout searchBar;

    private boolean view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(DataFramework.KEY_ID)) {
                search_entity = new EntitySearch(extras.getLong(DataFramework.KEY_ID));
            }
        }

        overridePendingTransition(R.anim.pull_in_to_up, R.anim.hold);

        if (search_entity == null) search_entity = new EntitySearch();

        setContentView(R.layout.search_activity);

        fragmentAdapter = new SearchFragmentAdapter(this, getSupportFragmentManager(), search_entity);

        pager = (ViewPager)findViewById(R.id.search_pager);
        pager.setAdapter(fragmentAdapter);

        searchRoot = (LinearLayout)findViewById(R.id.search_root);
        searchBar = (RelativeLayout)findViewById(R.id.search_bar_background);

        indicator = (TabPageIndicator)findViewById(R.id.search_indicator);
        indicator.setViewPager(pager);

        refreshTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SAVE_ID, 0,  R.string.save)
                .setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, SAVE_LAUNCH_ID, 0,  R.string.save_and_view)
                .setIcon(android.R.drawable.ic_menu_directions);
        return true;
    }

    public void refreshTheme() {

        BitmapDrawable bmp = (BitmapDrawable) getResources().getDrawable(themeManager.getResource("search_tile"));
        if (bmp != null) {
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            searchRoot.setBackgroundDrawable(bmp);
        }

        searchBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        themeManager.setColors();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case SAVE_ID:
                this.view = false;
                save();
                return true;
            case SAVE_LAUNCH_ID:
                this.view = true;
                save();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.push_out_from_up);
    }

    private void save() {
        String error = "";
        boolean save_tweets = false;

        String name_value = "";

        String searchAnd_value = fragmentAdapter.getSearchGeneralFragment().searchAnd.getText().toString();
        search_entity.setValue("words_and", searchAnd_value);

        if (!searchAnd_value.equals("")) if (name_value.length()<=0) name_value = searchAnd_value;

        String searchOr_value = fragmentAdapter.getSearchGeneralFragment().searchOr.getText().toString();
        search_entity.setValue("words_or", searchOr_value);

        if (!searchOr_value.equals("")) if (name_value.length()<=0) name_value = searchOr_value;

        String searchNot_value = fragmentAdapter.getSearchGeneralFragment().searchNot.getText().toString();
        search_entity.setValue("words_not", searchNot_value);

        if (!searchNot_value.equals("")) if (name_value.length()<=0) name_value = searchNot_value;

        String searchFromUser_value = fragmentAdapter.getSearchGeneralFragment().searchFromUser.getText().toString();
        search_entity.setValue("from_user", searchFromUser_value);

        if (!searchFromUser_value.equals("")) if (name_value.length()<=0) name_value = searchFromUser_value;

        String searchToUser_value = fragmentAdapter.getSearchGeneralFragment().searchToUser.getText().toString();
        search_entity.setValue("to_user", searchToUser_value);

        if (!searchToUser_value.equals("")) if (name_value.length()<=0) name_value = searchToUser_value;

        if (searchAnd_value.equals("") && searchOr_value.equals("") && searchNot_value.equals("") && searchFromUser_value.equals("") && searchToUser_value.equals("") ) {
            error = this.getString(R.string.error_search_text);
        }

        EditText name = fragmentAdapter.getSearchGeneralFragment().name;

        if (name.getText().toString().equals("")) {
            if (name_value.length() > 1)
                name_value = name_value.substring(0, 1).toUpperCase() + name_value.substring(1);

            search_entity.setValue("name", name_value);
        } else {
            search_entity.setValue("name", name.getText().toString());
        }

        long icon_id = fragmentAdapter.getSearchGeneralFragment().iconId;
        String token_file = fragmentAdapter.getSearchGeneralFragment().iconFile;

        search_entity.setValue("icon_id", icon_id);
        search_entity.setValue("icon_token_file", token_file);

        if (icon_id > 1) {
            Entity icon = new Entity("icons", icon_id);
            search_entity.setValue("icon_big", "drawable/"+icon.getValue("icon"));
            search_entity.setValue("icon_small", "drawable/"+icon.getValue("icon_small"));
        } else if (icon_id==1) {
            search_entity.setValue("icon_big", Utils.getIconGeneric(this, name_value));
            search_entity.setValue("icon_small", Utils.getIconGeneric(this, name_value)+"_small");
        } else {
            search_entity.setValue("icon_big", "file/"+token_file+".png");
            search_entity.setValue("icon_small", "file/"+token_file+"_small.png");
        }

        if (search_entity.getId() < 0) {
            search_entity.setValue("date_create", Utils.now());
            search_entity.setValue("last_modified", Utils.now());
            search_entity.setValue("use_count", 0);
        }

        Spinner languages = fragmentAdapter.getSearchAdvancedFragment().languages;

        if (languages.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
           String[] language_values = getResources().getStringArray(R.array.languages_values);
            search_entity.setValue("lang", language_values[languages.getSelectedItemPosition()]);
        }

        Spinner attitude = fragmentAdapter.getSearchAdvancedFragment().attitude;

        if (attitude.getSelectedItemPosition() != AdapterView.INVALID_POSITION)
            search_entity.setValue("attitude", attitude.getSelectedItemPosition());

        Spinner filter = fragmentAdapter.getSearchAdvancedFragment().filter;

        if (filter.getSelectedItemPosition() != AdapterView.INVALID_POSITION)
            search_entity.setValue("filter", filter.getSelectedItemPosition());

        CheckBox noRetweet = fragmentAdapter.getSearchAdvancedFragment().noRetweet;

        if (noRetweet.isChecked())
            search_entity.setValue("no_retweet", 1);
        else
            search_entity.setValue("no_retweet", 0);

        EditText searchSource = fragmentAdapter.getSearchAdvancedFragment().source;
        search_entity.setValue("source", searchSource.getText().toString());

        CheckBox notifications = fragmentAdapter.getSearchAdvancedFragment().notifications;

        // Borrar todos los tweets en el caso que deje de notificarse la búsqueda
        if (!notifications.isChecked() && search_entity.getInt("notifications") == 1) {
            DataFramework.getInstance().getDB().execSQL("DELETE FROM tweets WHERE search_id = " + search_entity.getId() + " AND favorite = 0");
            search_entity.setValue("last_tweet_id", "0");
            search_entity.setValue("last_tweet_id_notifications", "0");
            search_entity.setValue("new_tweets_count", "0");
        }

        // Guarda los primeros tweets en el caso de empezar a notificar
        if (notifications.isChecked() && search_entity.getInt("notifications")==0) {
            save_tweets = true;
        }

        if (notifications.isChecked())
            search_entity.setValue("notifications", 1);
        else
            search_entity.setValue("notifications", 0);


        CheckBox notificationsBar = fragmentAdapter.getSearchAdvancedFragment().notificationsBar;

        if (notificationsBar.isChecked())
            search_entity.setValue("notifications_bar", 1);
        else
            search_entity.setValue("notifications_bar", 0);

        if (fragmentAdapter.getSearchGeoFragment() != null) {
            CheckBox useGeolocation = fragmentAdapter.getSearchGeoFragment().useGeo;

            if (useGeolocation.isChecked()) {
                search_entity.setValue("use_geo", 1);

                RadioButton typeGeolocationGPS = fragmentAdapter.getSearchGeoFragment().typeGeoGPS;

                if (typeGeolocationGPS.isChecked())
                    search_entity.setValue("type_geo", 1);
                else
                    search_entity.setValue("type_geo", 0);

                EditText latitude = fragmentAdapter.getSearchGeoFragment().latitude;
                EditText longitude = fragmentAdapter.getSearchGeoFragment().longitude;

                try {
                    float latitude_value = Float.parseFloat(latitude.getText().toString());
                    float longitude_value = Float.parseFloat(longitude.getText().toString());

                    search_entity.setValue("latitude", latitude_value);
                    search_entity.setValue("longitude", longitude_value);
                } catch (Exception exception) {
                    error = this.getString(R.string.error_search_coord);
                }

                if (error.length()==0) {
                    SeekBar distance = fragmentAdapter.getSearchGeoFragment().distance;

                    if (distance.getProgress() > 0) {
                        search_entity.setValue("distance", distance.getProgress());

                        RadioButton typeDistanceKm = fragmentAdapter.getSearchGeoFragment().typeDistanceKM;

                        if (typeDistanceKm.isChecked()) {
                            search_entity.setValue("type_distance", 1);
                        } else {
                            search_entity.setValue("type_distance", 0);
                        }
                    } else {
                        error = this.getString(R.string.error_search_distance);
                    }
                }
            } else {
                search_entity.setValue("use_geo", 0);
            }
        } else {
            search_entity.setValue("use_geo", 0);
        }

        if (error.length() == 0) {
            if (save_tweets)
                saveTweets();
            else
                exitActivity();
        } else {
            Utils.showMessage(this, error);
        }
    }

    private void saveTweets() {
        progressDialog = ProgressDialog.show(this,getResources().getString(R.string.saved_tweet),getResources().getString(R.string.saved_tweet_description));

        APITweetTopics.execute(this, getSupportLoaderManager(), this, new SaveFirstTweetsRequest(this, search_entity.getId()));
    }

    private void exitActivity() {
        search_entity.setValue("is_temp", 0);
        search_entity.save();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.putExtra("view", this.view);
        intent.putExtra(DataFramework.KEY_ID, search_entity.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResults(BaseResponse response) {
        SaveFirstTweetsResponse result = (SaveFirstTweetsResponse)response;
        search_entity.setValue("last_tweet_id", result.getInfoSaveTweets().getOlderId());
        search_entity.setValue("last_tweet_id_notifications", result.getInfoSaveTweets().getNewerId());
        search_entity.setValue("new_tweets_count",result.getInfoSaveTweets().getNewMessages());

        exitActivity();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
    }
}