package course1778.mobileapp.safeMedicare.Helpers;

import android.content.Context;
import android.database.DatabaseUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-28.
 */
public class DrugInteractionCustomAdapter extends BaseAdapter implements ListAdapter {
	private ArrayList<String> list = new ArrayList<String>();
	private Context context;



	public DrugInteractionCustomAdapter(ArrayList<String> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int pos) {
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
//		return list.get(pos).getId();
		return pos;
		//just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final String drugName, drugInteractionName;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drug_interaction_item, null);
		}

		//Handle TextView and display string from your list
		drugName = list.get(position).split(Helpers.STRING_SPLITER)[0];
		drugInteractionName = list.get(position).split(Helpers.STRING_SPLITER)[1];

		TextView drug_name = (TextView)view.findViewById(R.id.drug1);
		TextView drug_interaction_name = (TextView)view.findViewById(R.id.drug2);
		drug_name.setText(drugName);
		drug_interaction_name.setText(drugInteractionName);

		//Handle buttons and add onClickListeners
		Button deleteBtn = (Button)view.findViewById(R.id.delete_button);

		deleteBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

				DatabaseInteractionHelper dbInteraction = new DatabaseInteractionHelper(context);
				dbInteraction.setDrugInteractionShow(
						drugName, drugInteractionName, DatabaseInteractionHelper.DRUG_INTERACTION_SHOW_FALSE);
				Log.d("mydeletebutton", DatabaseUtils.dumpCursorToString(dbInteraction.getCursor()));

				list.remove(position); //or some other task

				notifyDataSetChanged();
			}
		});

		return view;
	}
}
