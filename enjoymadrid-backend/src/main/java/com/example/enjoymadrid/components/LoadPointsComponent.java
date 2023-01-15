package com.example.enjoymadrid.components;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.enjoymadrid.services.AirQualityLoadService;
import com.example.enjoymadrid.services.TouristicLoadService;
import com.example.enjoymadrid.services.TransportLoadService;
import com.example.enjoymadrid.services.RefreshTokenService;

@Component
@EnableScheduling
public class LoadPointsComponent implements CommandLineRunner {

	private final RefreshTokenService refreshTokenService;
	private final AirQualityLoadService airQualityLoadService;
	private final TouristicLoadService touristicLoadService;
	private final TransportLoadService transportLoadService;
	
	public LoadPointsComponent(
			RefreshTokenService refreshTokenService,
			AirQualityLoadService airQualityLoadService,
			TouristicLoadService touristicLoadService,
			TransportLoadService transportLoadService
	) {
		this.refreshTokenService = refreshTokenService;
		this.airQualityLoadService = airQualityLoadService;
		this.touristicLoadService = touristicLoadService;
		this.transportLoadService = transportLoadService;
	}

	@Override
	public void run(String... args) throws Exception {
		// Add air quality stations if not already in DB and then update the air quality data
		//this.airQualityLoadService.loadAirQualityPoints();
		// Add places from Madrid city hall
		//this.touristicLoadService.loadTouristicPoints();
		// Load the information of all the transport points to DB if not already
		this.transportLoadService.loadTransportPoints();
	}
	
	/**
	 * This method is executed Mondays at 5am
	 * Purge expired refresh tokens. 
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 5 * * 0", zone = "Europe/Madrid")
	private void purgeExpiredTokens() {
		this.refreshTokenService.purgeExpiredTokens();
	}

	/**
	 * This method is executed six times a day (12 a.m / 4 a.m / 8 a.m / 12 p.m. / 4 p.m. / 8 p.m),
	 * updating the air quality data at each station. 
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0/4 * * *", zone = "Europe/Madrid")
	private void scheduleAqiPoints() {
		/*
		 * Execute the method updateAqiPoints at a random minute. Scheduled annotation
		 * (above) should end at minute 0. If pool tries to execute at minute 0, there
		 * might be a race condition with the actual thread running this block. In
		 * variable minuteExecuteUpdate we exclude the first minute for this reason.
		 */
		Integer minuteExecuteUpdate = 1 + new Random().nextInt(59);
		ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

		ex.schedule(() -> this.airQualityLoadService.updateAqiData(), minuteExecuteUpdate, TimeUnit.MINUTES);
	}

	/**
	 * This method is executed the first day of every month at 12:00 a.m. (and the first time the
	 * server is activated), checking for new information and deleting old information in tourist points.
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0 1 * *", zone = "Europe/Madrid")
	private void loadDataTouristicPoints() {
		this.touristicLoadService.loadTouristicPoints();
	}
		
	/**
	 * This method is executed all the days every 30 minutes (and the first time the
	 * server is activated), checking for new information and deleting old information.
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0/30 * * * ?", zone = "Europe/Madrid")
	private void updateBicyclePoints() {
		this.transportLoadService.updateBiciMADPoints();
	}
}
