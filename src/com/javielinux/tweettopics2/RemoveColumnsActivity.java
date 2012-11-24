package com.javielinux.tweettopics2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.ColumnsAdapter;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class RemoveColumnsActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<ColumnsAdapter.CheckedColumn> listColumns = new ArrayList<ColumnsAdapter.CheckedColumn>();
    private ThemeManager themeManager;
    private boolean changedColumns = false;
    private ColumnsAdapter adapter;
    private TextView message;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        themeManager = new ThemeManager(this);
        themeManager.setDialogTheme();
        themeManager.setColors();

        overridePendingTransition(R.anim.pull_in_to_up, R.anim.hold);

        setContentView(R.layout.remove_columns_activity);

        message = (TextView) findViewById(R.id.remove_columns_txt_message);

        gridView = (GridView) findViewById(R.id.remove_columns_gv_grid);

        findViewById(R.id.remove_columns_b_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeColumns();
            }
        });

        ArrayList<Entity> list = DataFramework.getInstance().getEntityList("columns", "", "position asc");
        for (Entity entity : list) {
            ColumnsAdapter.CheckedColumn checkedColumn = new ColumnsAdapter.CheckedColumn();
            checkedColumn.entity = entity;
            listColumns.add(checkedColumn);
        }

        adapter = new ColumnsAdapter(this, listColumns, new ColumnsAdapter.CheckedColumnListener() {
            @Override
            public void onChecked() {
                counterChecked();
            }
        });
        gridView.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    private void counterChecked() {
        int counter = 0;
        for (ColumnsAdapter.CheckedColumn checkedColumn : listColumns) {
            if (checkedColumn.checked) {
                counter++;
            }
        }
        if (counter>0) {
            message.setText(getString(R.string.columnsSelected, counter));
        } else {
            message.setText(getString(R.string.removeColumnsMessage));
        }
    }

    private void removeColumns() {
        for (ColumnsAdapter.CheckedColumn checkedColumn : listColumns) {
            if (checkedColumn.checked) {
                checkedColumn.entity.delete();
            }
        }
        Intent i = new Intent(this, TweetTopicsActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.push_out_from_up);
    }
}