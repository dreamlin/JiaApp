package org.baiyu.jiaapp.setting;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.bean.CityBean;
import org.baiyu.jiaapp.bean.WeatherBean;
import org.baiyu.jiaapp.bean.WeatherInfoBean;
import org.baiyu.jiaapp.customui.Topbar;
import org.baiyu.jiaapp.util.CommonAdapter;
import org.baiyu.jiaapp.util.DisplayUtil;
import org.baiyu.jiaapp.util.ViewHolder;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

/**
 * Created by baiyu on 2015-10-10.
 * 管理天气预报城市
 */
public class ManageCityActiviy extends Activity {

    private Topbar topbar;
    private SwipeMenuListView manage_city_smListView;

    private List<CityBean> cityBeanList;
    private CommonAdapter<CityBean> cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_city_activity);

        initView();
        initEvent();
    }

    private void initView() {
        topbar = (Topbar) findViewById(R.id.manage_city_topbar);
        manage_city_smListView = (SwipeMenuListView) findViewById(R.id.manage_city_smListView);
    }

    private void initEvent() {
        topbar.setOnTopbarClickListener(new Topbar.TopbarClickListener() {
            @Override
            public void leftClick() {
                ManageCityActiviy.this.finish();
            }

            @Override
            public void rightClick() {
                showDialog();
            }
        });

        cityBeanList = DataSupport.order("time desc").find(CityBean.class);
        cityAdapter = new CommonAdapter<CityBean>(this, cityBeanList, R.layout.manage_city_item) {
            @Override
            public void convert(ViewHolder holder, CityBean cityBean) {
                holder.setText(R.id.manage_city_item_tv_city_name, cityBean.getCityName());
            }
        };
        manage_city_smListView.setAdapter(cityAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                openItem.setWidth(DisplayUtil.dp2px(ManageCityActiviy.this, 80));
                // set item title
                openItem.setTitle("置顶");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DisplayUtil.dp2px(ManageCityActiviy.this, 80));

                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        manage_city_smListView.setMenuCreator(creator);

        // step 2. listener item click event
        manage_city_smListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                CityBean item = cityBeanList.get(position);
                switch (index) {
                    case 0:
                        item.setTime(new Date());
                        item.save();
                        cityBeanList.remove(position);
                        cityBeanList.add(0, item);
                        cityAdapter.notifyDataSetChanged();
                        Toast.makeText(ManageCityActiviy.this, "置顶", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        DataSupport.deleteAll(WeatherBean.class, "cityName = ?", item.getCityName());
                        DataSupport.deleteAll(WeatherInfoBean.class, "cityName = ?", item.getCityName());
                        item.delete();
                        cityBeanList.remove(position);
                        cityAdapter.notifyDataSetChanged();
                        Toast.makeText(ManageCityActiviy.this, "删除", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        manage_city_smListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityBean item = cityBeanList.get(position);
                Toast.makeText(ManageCityActiviy.this, item.getCityName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    AlertDialog dialog;

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.manage_city_add, null);

        ImageButton saveBtn = (ImageButton) view.findViewById(R.id.manage_city_add_save);
        final EditText cityNameText = (EditText) view.findViewById(R.id.manage_city_add_text);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityNameText.getText().toString();
                if (!"".equals(cityName)) {
                    cityNameText.setText("");
                    //下面代码用于失去焦点，隐藏键盘
//            cityNameText.clearFocus();
//            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(cityNameText.getWindowToken(), 0);

                    List<CityBean> configBeanList = DataSupport.where("cityName = ?", cityName).find(CityBean.class);
                    if (configBeanList.size() == 0) {
                        CityBean cityBean = new CityBean();
                        cityBean.setCityName(cityName);
                        cityBean.save();

                        cityBeanList.add(cityBean);
                        cityAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                    Toast.makeText(ManageCityActiviy.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();// 获取dialog
        dialog.show();// 显示对话框
    }
}
