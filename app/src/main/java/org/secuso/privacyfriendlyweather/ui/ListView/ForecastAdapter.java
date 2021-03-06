package org.secuso.privacyfriendlyweather.ui.ListView;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.UiUtils;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.ValueDeriver;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This class is the adapter for the ListView to render weather details of a day.
 */
public class ForecastAdapter extends ArrayAdapter<Forecast> {

    private Context context;
    private static LayoutInflater inflater = null;
    private RelativeLayout itemContainer;

    /**
     * @see ArrayAdapter#ArrayAdapter(Context, int, int, List)
     */
    public ForecastAdapter(Context context, int resource, List<Forecast> items) {
        super(context, resource, items);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemTemplate = inflater.inflate(R.layout.city_overview_list_item, null);
        itemContainer = (RelativeLayout) listItemTemplate.findViewById(R.id.city_overview_list_item_blank);
    }

    /**
     * @see ArrayAdapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            // Get the layout to use as well as the visual components
            vi = inflater.inflate(R.layout.details_overview_list_item, null);

            holder = new ViewHolder();
            holder.tvTime = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_time);
            holder.tvCategory = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_category_value);
            holder.tvTemperature = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_temperature_value);
            holder.tvHumidity = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_humidity_value);
            holder.tvPressure = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_pressure_value);
            holder.tvWindspeed = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_wind_speed_value);
            holder.tvRainVolume = (TextView) vi.findViewById(R.id.activity_city_weather_details_tv_rain_volume_value);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // Set the content
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        Forecast item = getItem(position);

        String temperature = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(item.getTemperature())),
                prefManager.getWeatherUnit()
        );
        String humidity = String.format("%s %%", item.getHumidity());
        String pressure = String.format("%s hPa", Math.round(item.getPressure()));
        String windSpeed = String.format("%s m/s", item.getWindSpeed());
        String rainVolume = (item.getPastRainVolume() == -1) ?
                context.getString(R.string.activity_city_weather_details_tv_rain_volume_na) :
                String.format("%s mm", item.getPastRainVolume());

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        IApiToDatabaseConversion.WeatherCategories category = IApiToDatabaseConversion.getLabelForValue(item.getWeatherID());
        ValueDeriver valueDeriver = new ValueDeriver(context);
        holder.tvTime.setText(dateFormatter.format(item.getForecastTime()));
        holder.tvCategory.setText(valueDeriver.getWeatherDescriptionByCategory(category));
        holder.tvTemperature.setText(temperature);
        holder.tvHumidity.setText(humidity);
        holder.tvPressure.setText(pressure);
        holder.tvWindspeed.setText(windSpeed);
        holder.tvRainVolume.setText(rainVolume);

        // Make sure the category is displayed entirely
        UiUtils.makeTextViewEntirelyVisible(itemContainer, holder.tvCategory);

        return vi;
    }

    /**
     * This class holds all visual fields that belong to one list item and are used to display
     * forecast information.
     */
    private static class ViewHolder {
        private TextView tvTime;
        private TextView tvCategory;
        private TextView tvTemperature;
        private TextView tvHumidity;
        private TextView tvPressure;
        private TextView tvWindspeed;
        private TextView tvRainVolume;
    }

}
