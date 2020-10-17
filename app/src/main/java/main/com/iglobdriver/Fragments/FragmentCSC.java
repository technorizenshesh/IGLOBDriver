package main.com.iglobdriver.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import main.com.iglobdriver.Adapters.AdapterCSC;
import main.com.iglobdriver.Interfaces.onClickCSC;
import main.com.iglobdriver.R;
import main.com.iglobdriver.constant.BaseUrl;
import www.develpoeramit.mapicall.ApiCallBuilder;


public class FragmentCSC extends BottomSheetDialogFragment implements onClickCSC {
    private CSC TYPE;
    private BottomSheetBehavior<View> behavior;
    private View view;
    private SwipeRefreshLayout swiperRefresh;
    private ListView list;
    private EditText et_search;
    private ArrayList<ModelCSC> arrayList = new ArrayList<>();
    private AdapterCSC adapter;
    private String mUrl="";
    private String ID;
    private onClickCSC callback;
    private HashMap<String,String>param;

    public static FragmentCSC get() {
        return new FragmentCSC();
    }
    public FragmentCSC setCallback(CSC csc, String id, onClickCSC clickCSC) {
        this.TYPE = csc;
        this.ID=id;
        this.callback=clickCSC;
        return this;
    }
    public FragmentCSC setCallback(onClickCSC clickCSC) {
        this.TYPE = CSC.Country;
        this.ID="0";
        this.callback=clickCSC;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_csc, null, false);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        findViews();
        return dialog;
    }

    private void findViews() {
        swiperRefresh = view.findViewById(R.id.swiperRefresh);
        list = view.findViewById(R.id.list);
        et_search = view.findViewById(R.id.et_search);
        adapter = new AdapterCSC(getActivity(), arrayList,this);
        list.setAdapter(adapter);
        switch (TYPE){
            case City:
                param=new HashMap<>();
                param.put("state_id",ID);
                et_search.setHint("Search City");
                mUrl= BaseUrl.get().getCity();
                break;
            case State:
                et_search.setHint("Search State");
                param=new HashMap<>();
                param.put("country_id",ID);
                mUrl=BaseUrl.get().getState();
                break;
            case Country:
                param=new HashMap<>();
                param.put("id","1");
                et_search.setHint("Search Country");
                mUrl=BaseUrl.get().getCountry();
                break;
        }

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getList();
    }

    @Override
    public void onResume() {
        super.onResume();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onStart() {
        super.onStart();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    void getList() {
        swiperRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(mUrl)
                .isShowProgressBar(false)
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        swiperRefresh.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                arrayList.add(new ModelCSC(jsonObject.getString("id"), jsonObject.getString("name")));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {

                    }
                });
    }

    @Override
    public void Result(ModelCSC csc) {
        callback.Result(csc);
        dismiss();
    }
}
