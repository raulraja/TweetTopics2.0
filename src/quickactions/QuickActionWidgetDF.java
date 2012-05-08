package quickactions;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionWidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.cyrilmottier.android.greendroid.R;

public class QuickActionWidgetDF extends QuickActionWidget {

	public interface QuickActionWidgetDFGetView {
	    public abstract View OnGetView(int position, View view, ViewGroup parent);
	}
	
	public interface QuickActionWidgetDFButtonOnClick {
	    public abstract void OnClick();
	}
	
	public static int TYPE_LIST = 0;
	public static int TYPE_GRID = 1;

	private QuickActionWidgetDFGetView OnGetView = null;
	
	private ListView mListView;
	private GridView mGridView;
	
	protected ArrayList<QuickActionWidgetDFButtonOnClick> mButtonOnClick = new ArrayList<QuickActionWidgetDFButtonOnClick>();
	
	protected ArrayList<Entity> mEntities;
	private Context mContext;
	private String mTable = "";
	private String mWhere = "";
	private String mOrder = "";
	private int mType = TYPE_LIST;
	private BaseAdapter mAdapter;
	
	public QuickActionWidgetDF(Context context, int type, String table) {
		super(context);
		mContext = context;	
		mType = type;
		mTable = table;
		init();
	}
	
	public QuickActionWidgetDF(Context context, int type, String table, String where) {
		super(context);
		mContext = context;	
		mType = type;
		mTable = table;
		mWhere = where;
		init();
	}
	
	public QuickActionWidgetDF(Context context, int type, String table, String where, String order) {
		super(context);
		mContext = context;
		mType = type;
		mTable = table;
		mWhere = where;
		mOrder = order;
		init();
	}
	
	public void init() {
		mEntities = DataFramework.getInstance().getEntityList(mTable, mWhere, mOrder);
		
		setContentView(R.layout.gd_quick_action_list_df);

        final View v = getContentView();
        mListView = (ListView) v.findViewById(R.id.gdi_list);
        mGridView = (GridView) v.findViewById(R.id.gdi_grid);
        
        if (mType==TYPE_LIST) {
        	mListView.setVisibility(View.VISIBLE);
        	mGridView.setVisibility(View.GONE);
        } else {
        	mListView.setVisibility(View.GONE);
        	mGridView.setVisibility(View.VISIBLE);
        }
	}
	
	public void refresh() {
		mEntities.clear();
		mEntities = DataFramework.getInstance().getEntityList(mTable, mWhere, mOrder);
		populateQuickActions(null);
	}
	
	public ArrayList<Entity> getEntities() {
		return mEntities;
	}
	
	public void setOnGetView(QuickActionWidgetDFGetView getView) {
		OnGetView = getView;
	}
	
	public void addButton(int resTitle, QuickActionWidgetDFButtonOnClick onClick) {
				
		Button bt = new Button(mContext);
		bt.setText(resTitle);
		bt.setTag(mButtonOnClick.size());
        
        bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getDismissOnClick()) {
	                dismiss();
	            }
				mButtonOnClick.get(Integer.parseInt(v.getTag().toString())).OnClick();
				//mTweetTopicsCore.newUser();	
			}
        	
        });
        
        mButtonOnClick.add(onClick);
        
        ((LinearLayout) getContentView().findViewById(R.id.buttons_layout)).addView(bt);
        
	}

	@Override
	protected void populateQuickActions(List<QuickAction> quickActions) {
		mAdapter = new BaseAdapter() {

            public View getView(int position, View view, ViewGroup parent) {

            	return OnGetView.OnGetView(position, view, parent);
            	
            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return mEntities.size();
            }
        };
		
		if (mType==TYPE_LIST) {
			mListView.setAdapter(mAdapter);
	    	mListView.setOnItemClickListener(mInternalItemClickListener);
        } else {
        	mGridView.setAdapter(mAdapter);
	    	mGridView.setOnItemClickListener(mInternalItemClickListener);
        }
    	
	}

	@Override
	protected void onMeasureAndLayout(Rect anchorRect, View contentView) {
        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        contentView.measure(MeasureSpec.makeMeasureSpec(getScreenWidth(), MeasureSpec.EXACTLY),
        		LayoutParams.WRAP_CONTENT);

        int rootHeight = contentView.getMeasuredHeight();

        int offsetY = getArrowOffsetY();
        int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom);
        int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY : anchorRect.bottom - offsetY;

        setWidgetSpecs(popupY, onTop);
	}
	
    private OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            getOnQuickActionClickListener().onQuickActionClicked(QuickActionWidgetDF.this, position);
            if (getDismissOnClick()) {
                dismiss();
            }
        }
    };
    
    public void show(View anchor) {
    	mIsDirty = true;
    	super.show(anchor);
    }

}
