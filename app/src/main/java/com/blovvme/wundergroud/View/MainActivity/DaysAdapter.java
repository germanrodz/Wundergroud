package com.blovvme.wundergroud.View.MainActivity;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blovvme.wundergroud.Model.HourlyForecastOrdered;
import com.blovvme.wundergroud.R;

import java.util.List;


public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {
    List<HourlyForecastOrdered> hourlyForecastOrdered;
    Context context;
    private int lastPosition = -1;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    private String unit;

    public DaysAdapter(List<HourlyForecastOrdered> hourlyForecastOrdered) {
        this.hourlyForecastOrdered = hourlyForecastOrdered;
    }

    public void setUnits(String unit) {
        this.unit = unit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        RecyclerView rvWeather;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDay = (TextView) itemView.findViewById(R.id.tvDay);
            rvWeather = (RecyclerView) itemView.findViewById(R.id.rvWeather);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.day_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HourlyForecastOrdered item = hourlyForecastOrdered.get(position);
        holder.tvDay.setText(item.getLabel());
        layoutManager = new GridLayoutManager(context,4);
        itemAnimator = new DefaultItemAnimator();
        holder.rvWeather.setLayoutManager(layoutManager);
        holder.rvWeather.setItemAnimator(itemAnimator);

        WeatherAdapter weatherAdapter = new WeatherAdapter(item.getHourlyForecastOrdered());
        holder.rvWeather.setAdapter(weatherAdapter);
        weatherAdapter.setUnits(unit);
        weatherAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return hourlyForecastOrdered.size();
    }



}
