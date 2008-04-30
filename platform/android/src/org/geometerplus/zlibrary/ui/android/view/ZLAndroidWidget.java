/*
 * Copyright (C) 2007-2008 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.view.*;
import android.util.AttributeSet;

import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;

import org.geometerplus.zlibrary.ui.android.util.ZLAndroidKeyUtil;

public class ZLAndroidWidget extends View {
	private final ZLAndroidPaintContext myPaintContext = new ZLAndroidPaintContext();
	private ZLAndroidViewWidget myViewWidget;

	public ZLAndroidWidget(Context context, AttributeSet attrs, Map inflateParams, int defStyle) {
		super(context, attrs, inflateParams, defStyle);
		setFocusable(true);
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs, Map inflateParams) {
		super(context, attrs, inflateParams);
		setFocusable(true);
	}

	public ZLAndroidPaintContext getPaintContext() {
		return myPaintContext;
	}

	void setViewWidget(ZLAndroidViewWidget viewWidget) {
		myViewWidget = viewWidget;
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (myViewWidget == null) {
			return;
		}
		ZLView view = myViewWidget.getView();
		if (view == null) {
			return;
		}

		final int w = getWidth();
		final int h = getHeight();

		myPaintContext.beginPaint(canvas);
		long start = System.currentTimeMillis();
		final int rotation = myViewWidget.getRotation();
		myPaintContext.setRotation(rotation);
		switch (rotation) {
			case ZLViewWidget.Angle.DEGREES0:
				myPaintContext.setSize(w, h);
				break;
			case ZLViewWidget.Angle.DEGREES90:
				myPaintContext.setSize(h, w);
				canvas.rotate(270, h / 2, h / 2);
				break;
			case ZLViewWidget.Angle.DEGREES180:
				myPaintContext.setSize(w, h);
				canvas.rotate(180, w / 2, h / 2);
				break;
			case ZLViewWidget.Angle.DEGREES270:
				myPaintContext.setSize(h, w);
				canvas.rotate(90, w / 2, w / 2);
				break;
		}
		view.paint();
		myPaintContext.endPaint();
	}

	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch (myViewWidget.getRotation()) {
			case ZLViewWidget.Angle.DEGREES0:
				break;
			case ZLViewWidget.Angle.DEGREES90:
			{
				int swap = x;
				x = getHeight() - y - 1;
				y = swap;
				break;
			}
			case ZLViewWidget.Angle.DEGREES180:
			{
				x = getWidth() - x - 1;
				y = getHeight() - y - 1;
				break;
			}
			case ZLViewWidget.Angle.DEGREES270:
			{
				int swap = getWidth() - x - 1;
				x = y;
				y = swap;
				break;
			}
		}

		ZLView view = myViewWidget.getView();
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				view.onStylusRelease(x, y);
				break;
			case MotionEvent.ACTION_DOWN:
				view.onStylusPress(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				view.onStylusMovePressed(x, y);
				break;
		}

		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final String keyName = ZLAndroidKeyUtil.getKeyNameByCode(keyCode);
		if (keyName.equals("<Menu>")) {
			return false;
		}
		myViewWidget.getView().Application.doActionByKey(keyName);
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		final String keyName = ZLAndroidKeyUtil.getKeyNameByCode(keyCode);
		if (keyName.equals("<Menu>")) {
			return false;
		}
		return true;
	}
}
