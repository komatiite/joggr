package com.example.kcroz.joggr.ListRuns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.kcroz.joggr.JoggrHelper;
import com.example.kcroz.joggr.R;
import com.example.kcroz.joggr.RunData;
import com.example.kcroz.joggr.ViewRoute.ViewRouteActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MonthlyFragment extends Fragment {

    private ListView lvRuns;
    private List<Map<String,String>> mapList;
    private ArrayList<RunData> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_list, container, false);
        lvRuns = view.findViewById(R.id.lvRuns);

        mapList = new ArrayList<>();
        data = getArguments().getParcelableArrayList("RunData");

        convertObjectDataToMap();
        createListView();
        setOnItemClickListener();

        return view;
    }

    private void convertObjectDataToMap() {
        for (int i = 0; i < data.size(); i++) {
            Log.d("In fragment", data.get(i).getDate());

            int day = Integer.parseInt(String.valueOf(data.get(i).getDate().charAt(12)) + data.get(i).getDate().charAt(13));
            int month = Integer.parseInt(String.valueOf(data.get(i).getDate().charAt(7)) + data.get(i).getDate().charAt(8));
            String monthName = JoggrHelper.getMonthName(month);



            Map<String,String> map = new HashMap<>();

            map.put("RunID", String.valueOf(data.get(i).getRunID()));
            //map.put("Date", data.get(i).getDate());
            map.put("Date", monthName + " " + day);

            //map.put("Title", data.get(i).getTitle());
            String title = data.get(i).getTitle();

            //Log.d("Titles: ", title);

            if (title == null) {
                title = "Assiniboine Park Run";
            }

            map.put("Title", title);
            map.put("Distance", String.valueOf((float)(int)data.get(i).getDistance() / 1000) + " km");
            map.put("RunTime", data.get(i).getRunTime());
            map.put("Rating", String.valueOf(data.get(i).getRating()));
            map.put("Comment", data.get(i).getComment());
            map.put("Icon", R.drawable.icon_jog + "");

            mapList.add(map);
        }
    }

    private void createListView() {
        //int[] images = { R.drawable.icon_jog };

        /*SimpleAdapter adapter = new SimpleAdapter(getContext(),
                mapList,
                R.layout.listview_run,
                new String[] {"RunID", "Date"},
                new int[] {R.id.tvRunID, R.id.tvDate});*/

        SimpleAdapter adapter = new SimpleAdapter(getContext(),
                mapList,
                R.layout.fragment_run_list_item,
                new String[] {"Icon", "Date", "Title", "Distance"},
                //new String[] { "Title", "Date", "Distance" },
                new int[] { R.id.ivRunIcon, R.id.tvRunDate, R.id.tvRunTitle, R.id.tvRunDistance });
                //new int[] { R.id.tvRunDate, R.id.tvRunTitle, R.id.tvRunDistance });

        lvRuns.setAdapter(adapter);
    }

    private void setOnItemClickListener() {
        lvRuns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                HashMap<String,String> hashMap = (HashMap<String,String>)lvRuns.getItemAtPosition(myItemInt);
                String runID = hashMap.get("RunID");

                Intent routeIntent = new Intent(getActivity(), ViewRouteActivity.class);
                routeIntent.putExtra("RUN_ID", runID);

                getActivity().startActivity(routeIntent);
            }
        });
    }
}