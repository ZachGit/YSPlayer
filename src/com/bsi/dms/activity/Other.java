package com.bsi.dms.activity;

import com.bsi.dms.bean.Programtask;

public class Other {
	private String id;
	private String title;
	private Programtask obj;
	private boolean hasParent;
	private boolean hasChild;
	private String parent;
	private int level;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public Programtask getobj() {
		return obj;
	}

	public void setTitle(String outlineTitle) {
		this.title = outlineTitle;
	}

	public void setObj(Programtask obj) {
		this.obj = obj;
	}

	public boolean isHasParent() {
		return hasParent;
	}

	public void setHasParent(boolean mhasParent) {
		this.hasParent = mhasParent;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean mhasChild) {
		this.hasChild = mhasChild;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	// private OutlineElement outlineElement;
	private boolean expanded;

	/**
	 * 
	 * @param id
	 *            id
	 * @param title
	 *            鏍囬
	 * @param hasParent
	 *            鏄惁瀛樺湪鐖剁骇
	 * @param hasChild
	 *            鏄惁瀛樺湪瀛愮骇
	 * @param parent
	 *            鐖剁骇鏍囧織
	 * @param level
	 *            绾у埆
	 * @param expanded
	 *            鏄惁灞曞紑
	 */
	public Other(String id, String title, Programtask obj, boolean hasParent,
			boolean hasChild, String parent, int level, boolean expanded) {
		super();
		this.id = id;
		this.title = title;
		this.obj = obj;
		this.hasParent = hasParent;
		this.hasChild = hasChild;
		this.parent = parent;
		this.level = level;
		this.expanded = expanded;
	}
}
