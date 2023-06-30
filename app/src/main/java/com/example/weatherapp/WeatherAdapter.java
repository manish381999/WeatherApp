package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.WeatherItemLayoutBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter  extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>{
Context context;
ArrayList<WeatherModel> list;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_item_layout,parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        WeatherModel model=list.get(position);

        Picasso.get().load("http:".concat(model.getIcon())).into(holder.binding.ivCondition);
        holder.binding.tvTemperature.setText(model.getTemperature() + "°c");
        holder.binding.tvWindSpeed.setText(model.getWindSpeed() + "Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("mm-dd aa ");
        try{
            Date t =input.parse(model.getTime());
            holder.binding.tvTime.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder{
WeatherItemLayoutBinding binding;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=WeatherItemLayoutBinding.bind(itemView);
        }
    }
}
