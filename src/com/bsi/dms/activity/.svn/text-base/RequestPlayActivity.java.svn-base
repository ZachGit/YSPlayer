package com.bsi.dms.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsi.dms.R;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.Programtask;
import com.bsi.dms.player.PlayerController;

public class RequestPlayActivity extends ListActivity {
	private ArrayList<Other> group = new ArrayList<Other>();
	private ArrayList<Other> child = new ArrayList<Other>();
	private TreeViewAdapter treeViewAdapter = null;
	private ImageView image;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		treeViewAdapter = new TreeViewAdapter(this, R.layout.activity_requestplay, group);
		initialData();
		setListAdapter(treeViewAdapter);
		registerForContextMenu(getListView());
	}

	private void initialData() {
		image = (ImageView) findViewById(R.id.image_list);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;

		for (Programtask info : PlayerController.getInstance().getProgramtasks()) {
			if (info.isLoopProgram()) {
				continue;
			}
			item = new HashMap<String, Object>();
			// item.put("programname", info.getProgramname());
			// item.put("programname", CommonUtil.getUTF8XMLString(
			// info.getProgramname() ) );

			item.put("obj", info);
			Other other1 = new Other(info.getPlayerid(), info.getProgramname(),
					info, false, false, "00", 0, false);
			group.add(other1);
			child.add(other1);
		}
		for (Programtask insItem : PlayerController.getInstance().getInsProgramtasks() ) {
			if (insItem.isLoopProgram()) {
				continue;
			}
			item = new HashMap<String, Object>();
			// item.put("programname", insItem.getProgramname());
			// item.put("programname", CommonUtil.getUTF8XMLString(
			// insItem.getProgramname()) );

			item.put("obj", insItem);
			Other other2 = new Other(insItem.getPlayerid(),
					insItem.getProgramname(), insItem, false, false, "00", 0,
					false);
			group.add(other2);
			child.add(other2);
		}

		for (Playlist loopitem : PlayerController.getInstance().getLoopplaylists() ) {
			List<Programtask> protasks = loopitem.getProgramtasks();
			Other other3 = new Other(loopitem.getPlaylistid(),
					loopitem.getPlaylistname(), null, false, true, "00", 0,
					false);
			group.add(other3);
			child.add(other3);
			for (Programtask program : protasks) {
				item = new HashMap<String, Object>();
				// item.put("programname", program.getProgramname());
				item.put("obj", program);
				// data.add(item);
				Other other4 = new Other(program.getPlayerid(),
						program.getProgramname(), program, true, false,
						loopitem.getPlaylistid(), 1, false);
				// image.setVisibility(View.VISIBLE);
				child.add(other4);

			}
		}
	}

	private class TreeViewAdapter extends ArrayAdapter {
		private LayoutInflater inflater;
		private List<Other> fileList;
		private Bitmap imgHe;
		private Bitmap imgKai;

		public TreeViewAdapter(Context context, int textViewResourceId,
				List objects) {
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
			fileList = objects;
			imgHe = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.outline_list_collapse);
			imgKai = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.outline_list_expand);
		}

		public int getCount() {
			return fileList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			/* if (convertView == null) { */
			convertView = inflater.inflate(R.layout.activity_requestplay, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.image_list);
			convertView.setTag(holder);
			/*
			 * } else { holder = (ViewHolder) convertView.getTag(); }
			 */

			int level = fileList.get(position).getLevel();
			holder.icon.setPadding(25 * (level + 1),
					holder.icon.getPaddingTop(), 0,
					holder.icon.getPaddingBottom());
			holder.text.setText(fileList.get(position).getTitle());
			// 有子元素且未展开
			if (fileList.get(position).isHasChild()
					&& (fileList.get(position).isExpanded() == false)) {
				holder.icon.setImageBitmap(imgHe);
				// 有子元素且展开
			} else if (fileList.get(position).isHasChild()
					&& (fileList.get(position).isExpanded() == true)) {
				holder.icon.setImageBitmap(imgKai);
				// 无子元素
			} else if (!fileList.get(position).isHasChild()) {
				holder.icon.setImageBitmap(imgHe);
				holder.icon.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			TextView text;
			ImageView icon;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// 无子元素,说明是最后一级
		if (!group.get(position).isHasChild()) {
			// stop play
			PlayerController.getInstance().stopPlay();

			Programtask p = group.get(position).getobj();
			PlayerController.getInstance().setChangeScreen(p);
			RequestPlayActivity.this.finish();
			return;
		}

		// 当前关闭
		// 执行展开操作
		if (!group.get(position).isExpanded()) {
			group.get(position).setExpanded(true);
			int level = group.get(position).getLevel();
			int nextLevel = level + 1;

			for (Other other : child) {
				int j = 1;
				if (other.getParent() == group.get(position).getId()) {
					other.setLevel(nextLevel);
					other.setExpanded(false);
					group.add(position + j, other);
					j++;
				}
			}
			treeViewAdapter.notifyDataSetChanged();
		}

		// 当前展开
		// 执行关闭操作
		else {
			group.get(position).setExpanded(false);
			Other other = group.get(position);
			ArrayList<Other> temp = new ArrayList<Other>();
			for (int i = position + 1; i < group.size(); i++) {
				if (other.getLevel() >= group.get(i).getLevel()) {
					break;
				}
				temp.add(group.get(i));
			}
			group.removeAll(temp);
			treeViewAdapter.notifyDataSetChanged();
		}
	}
}
