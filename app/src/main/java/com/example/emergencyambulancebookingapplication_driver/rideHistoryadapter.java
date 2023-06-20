package com.example.emergencyambulancebookingapplication_driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class rideHistoryadapter extends RecyclerView.Adapter<rideHistoryadapter.myviewholder> {
    ArrayList<rideHistoryModel> datalist;

    public rideHistoryadapter(ArrayList<rideHistoryModel> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {


        holder.ambulanceCategory.setText(datalist.get(position).getAmbulanceCategory());
        holder.date.setText(datalist.get(position).getDate());
        holder.dropOff.setText(datalist.get(position).getDropOffLatLng());
        holder.fare.setText(datalist.get(position).getFare());
        holder.pickUp.setText(datalist.get(position).getPickUpLatLng());
        holder.time.setText(datalist.get(position).getTime());
        holder.distance.setText(datalist.get(position).getDistance());
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView ambulanceCategory, date, dropOff, fare, pickUp, time, distance;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            ambulanceCategory = itemView.findViewById(R.id.ambulanceCategoryId);
            date = itemView.findViewById(R.id.dateId);
            dropOff = itemView.findViewById(R.id.dropOffId);
            fare = itemView.findViewById(R.id.fareId);
            pickUp = itemView.findViewById(R.id.pickUpId);
            time = itemView.findViewById(R.id.timeId);
            distance = itemView.findViewById(R.id.distanceId);
        }
    }
}
