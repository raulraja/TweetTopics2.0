package infos;

import adapters.ResponseListAdapter;

public class InfoTweetList {

	private ResponseListAdapter mResponseListAdapter;
	private int mPosition = 0;
	private int mColumn;
	private long mSearch;
	private int mTypeList;
	private int mTypeStatus;
	private String mTextTypeStatus;
	
	public InfoTweetList() {

	}

	public void setResponseListAdapter(ResponseListAdapter mResponseListAdapter) {
		this.mResponseListAdapter = mResponseListAdapter;
	}

	public ResponseListAdapter getResponseListAdapter() {
		return mResponseListAdapter;
	}

	public void setSearch(long mSearch) {
		this.mSearch = mSearch;
	}

	public long getSearch() {
		return mSearch;
	}

	public void setColumn(int mColumn) {
		this.mColumn = mColumn;
	}

	public int getColumn() {
		return mColumn;
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setTypeList(int mTypeList) {
		this.mTypeList = mTypeList;
	}

	public int getTypeList() {
		return mTypeList;
	}

	public void setTypeStatus(int mTypeStatus) {
		this.mTypeStatus = mTypeStatus;
	}

	public int getTypeStatus() {
		return mTypeStatus;
	}

	public void setTextTypeStatus(String mTextTypeStatus) {
		this.mTextTypeStatus = mTextTypeStatus;
	}

	public String getTextTypeStatus() {
		return mTextTypeStatus;
	}

	
}
