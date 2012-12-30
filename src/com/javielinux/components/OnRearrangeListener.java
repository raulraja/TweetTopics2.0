/**
 * https://github.com/thquinn/DraggableGridView
 */

package com.javielinux.components;

public interface OnRearrangeListener {
	
	public abstract void onRearrange(int oldIndex, int newIndex);
    public abstract void onStartDrag(int x, int index);
    public abstract void onMoveDragged(int index);
}
