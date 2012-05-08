/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quickactions;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionWidget;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.javielinux.tweettopics.R;


/**
 * A {@link QuickActionGridTree} is an implementation of a {@link QuickActionWidget}
 * that displays {@link QuickAction}s in a grid manner. This is usually used to create
 * a shortcut to jump between different type of information on screen.
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 */
public class QuickActionGridTree extends QuickActionTreeWidget {

	public static int GRID_PARENT = -1;
	
    private GridView mMainGridView;
    private GridView mChildGridView;
    private Context mContext;
    private int mCurrentParent = GRID_PARENT;
    private LinearLayout mGridTitle;
    
    private ImageView mQuickActionImageMenu;
    private ImageView mQuickActionImageMenuBack;
    private TextView mQuickActionTextMenu;
    private QuickAction mQuickActionMenu;

    public QuickActionGridTree(Context context, QuickAction action) {
        super(context);

        mContext = context;
        
        setContentView(R.layout.gd_quick_action_grid_tree);
        
        mQuickActionMenu = action;

        final View v = getContentView();
        mMainGridView = (GridView) v.findViewById(R.id.gdi_grid);
        
        mChildGridView = (GridView) v.findViewById(R.id.gdi_grid_child);
        
        mGridTitle = (LinearLayout) v.findViewById(R.id.gdi_title);
        
        mGridTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentParent!=GRID_PARENT) {
					showParent();
				}
			}
        	
        });
        
        mQuickActionImageMenuBack = (ImageView) v.findViewById(R.id.img_title_back);
        
        mQuickActionImageMenu = (ImageView) v.findViewById(R.id.img_title);
        mQuickActionImageMenu.setImageDrawable(mQuickActionMenu.mDrawable);
        
        mQuickActionTextMenu = (TextView) v.findViewById(R.id.txt_title);
        mQuickActionTextMenu.setText(mQuickActionMenu.mTitle);
        
    }

    protected void createChildQuickActions(final List<QuickAction> quickActions) {

    	mChildGridView.setAdapter(new BaseAdapter() {

            public View getView(int position, View view, ViewGroup parent) {

                TextView textView = (TextView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    textView = (TextView) inflater.inflate(R.layout.gd_quick_action_grid_tree_item, mChildGridView, false);
                }

                QuickAction quickAction = quickActions.get(position);
                textView.setText(quickAction.mTitle);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, quickAction.mDrawable, null, null);

                return textView;

            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return quickActions.size();
            }
        });

    	mChildGridView.setOnItemClickListener(mInternalItemClickListener);
    }
    
    @Override
    protected void populateQuickActions(final List<QuickAction> quickActions) {

        mMainGridView.setAdapter(new BaseAdapter() {

            public View getView(int position, View view, ViewGroup parent) {

                TextView textView = (TextView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    textView = (TextView) inflater.inflate(R.layout.gd_quick_action_grid_tree_item, mMainGridView, false);
                }

                QuickAction quickAction = quickActions.get(position);
                textView.setText(quickAction.mTitle);
                
                if (QuickActionGridTree.this.hasChilds(quickAction)) {
                	textView.setCompoundDrawablesWithIntrinsicBounds(null, quickAction.mDrawable, 
                			mContext.getResources().getDrawable(R.drawable.more_quickactions), null);
                } else {
                	textView.setCompoundDrawablesWithIntrinsicBounds(null, quickAction.mDrawable, 
                			mContext.getResources().getDrawable(R.drawable.more_quickactions_empty), null);
                }

                return textView;

            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return quickActions.size();
            }
        });

        mMainGridView.setOnItemClickListener(mInternalItemClickListener);
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
        	QuickAction quickAction = null;
        	if (mCurrentParent == GRID_PARENT) {
        		quickAction = (QuickAction)mQuickActions.get(position);
        	}
        	if (quickAction!=null && QuickActionGridTree.this.hasChilds(quickAction)) {
        		mCurrentParent = position;
        		createChildQuickActions(QuickActionGridTree.this.getChildsQuickACtions(quickAction));
        		showChild(quickAction);
        	} else {
	            getOnQuickActionClickListener().onQuickActionTreeClicked(QuickActionGridTree.this, mCurrentParent, position);
	            if (getDismissOnClick()) {
	            	close();
	            }
        	}
        }
    };
    
    private void close() {
    	mCurrentParent = GRID_PARENT;
    	mMainGridView.setVisibility(View.VISIBLE);
    	mChildGridView.setVisibility(View.GONE);
    	mQuickActionImageMenu.setImageDrawable(mQuickActionMenu.mDrawable);
    	mQuickActionTextMenu.setText(mQuickActionMenu.mTitle);
    	dismiss();
    }
    
    private void showChild(QuickAction action) {
    	showChildButtons(action);
    }
    
    private void showChildButtons(final QuickAction action) {
	    LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mContext, R.anim.outlayout_to_left);
	    mMainGridView.setLayoutAnimation(lac);
	    mMainGridView.setLayoutAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation lac) {
            	showChildTitle(action);
            	mMainGridView.setVisibility(View.INVISIBLE);
            	mChildGridView.setVisibility(View.VISIBLE);
            	LayoutAnimationController lac2 = AnimationUtils.loadLayoutAnimation(mContext, R.anim.inlayout_from_right);
            	mChildGridView.setLayoutAnimation(lac2);
            	mChildGridView.setLayoutAnimationListener(null);
            	mChildGridView.startLayoutAnimation();
            }
    
            @Override
            public void onAnimationRepeat(Animation animation) { }
    
            @Override
            public void onAnimationStart(Animation animation) { }
    
        });
	    mMainGridView.startLayoutAnimation();
    }
    
    private void showChildTitle(QuickAction action) {
    	mQuickActionImageMenu.setImageDrawable(action.mDrawable);
    	mQuickActionTextMenu.setText(action.mTitle);
    	mQuickActionImageMenuBack.setVisibility(View.VISIBLE);
    	LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mContext, R.anim.inlayout_from_right);
	    mGridTitle.setLayoutAnimation(lac);
	    mGridTitle.setLayoutAnimationListener(null);
	    mGridTitle.startLayoutAnimation();
    }
    
    
    private void showParent() {
    	showParentButtons();
    }
    
    private void showParentTitle() {
    	mQuickActionImageMenu.setImageDrawable(mQuickActionMenu.mDrawable);
    	mQuickActionTextMenu.setText(mQuickActionMenu.mTitle);
    	mQuickActionImageMenuBack.setVisibility(View.GONE);
    	
	    LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mContext, R.anim.inlayout_from_left);
	    mGridTitle.setLayoutAnimation(lac);
	    mGridTitle.setVisibility(View.INVISIBLE);
	    mGridTitle.setVisibility(View.VISIBLE);
	    mGridTitle.setLayoutAnimationListener(null);
	    mChildGridView.startLayoutAnimation();
	    
    }
    
    private void showParentButtons() {
    	mCurrentParent = GRID_PARENT;
	    LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mContext, R.anim.outlayout_to_right);
	    mChildGridView.setLayoutAnimation(lac);
	    mChildGridView.setLayoutAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation lac) {
            	showParentTitle();
            	mChildGridView.setVisibility(View.INVISIBLE);
            	mMainGridView.setVisibility(View.VISIBLE);
            	LayoutAnimationController lac2 = AnimationUtils.loadLayoutAnimation(mContext, R.anim.inlayout_from_left);
            	mMainGridView.setLayoutAnimation(lac2);
            	mMainGridView.setLayoutAnimationListener(null);
            	mMainGridView.startLayoutAnimation();
            }
    
            @Override
            public void onAnimationRepeat(Animation animation) { }
    
            @Override
            public void onAnimationStart(Animation animation) { }
    
        });
	    mChildGridView.startLayoutAnimation();
    }
    


}
