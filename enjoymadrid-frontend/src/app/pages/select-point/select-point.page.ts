import { Component, HostListener, Input, OnInit } from '@angular/core';
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch';
import { Geolocation } from '@awesome-cordova-plugins/geolocation/ngx';
import * as L from 'leaflet';
import { SharedService } from 'src/app/services/shared/shared.service';
import { ModalController } from '@ionic/angular';
import { PointModel } from 'src/app/models/point.model';
import { RouteService } from 'src/app/services/route/route.service';

@Component({
  selector: 'app-select-point',
  templateUrl: './select-point.page.html',
  styleUrls: ['./select-point.page.scss'],
})
export class SelectPointPage implements OnInit {

  // Information passed from create-route page
  @Input() isOrigin: boolean;
  @Input() pointEmpty: boolean;
  @Input() point: PointModel;

  // For the geo search
  provider = new OpenStreetMapProvider();
  // Map & markers
  map: L.Map;
  marker: L.Marker;
  // Reference to Search control
  searchControl: any;

  constructor(
    private geolocation: Geolocation,
    private sharedService: SharedService,
    private routeService: RouteService,
    private modalController: ModalController
  ) { }

  ngOnInit() {
  }

  ionViewDidEnter() {

    // OpenStreetMap
    // https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png
    const standard = L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' + 
      '&copy; <a href="https://carto.com/attributions#basemaps">CARTO</a>'
    });

    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; <a href="https://www.esri.com/en-us/home"> Esri</a>'
    });

    const layers = {
      "<span style='cursor: pointer'>Satélite</span>": satellite,
      "<span style='cursor: pointer'>Predeterminado</span>": standard
    };

    this.searchControl = GeoSearchControl({
      provider: new OpenStreetMapProvider(),
      style: 'bar',
      autoCompleteDelay: 500,
      showMarker: false,
      retainZoomLevel: true,
      searchLabel: 'Buscar o clicar mapa',
      notFoundMessage: 'No se ha podido encontrar la dirección',
    });

    this.map = L.map('map', {
      zoomControl: false,
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    if (!this.pointEmpty) { 
      this.marker = L.marker([this.point.latitude, this.point.longitude]);
      this.marker.addTo(this.map);
      this.map.setView([this.point.latitude, this.point.longitude], 18);
    }

    this.map.addControl(this.searchControl);
    L.control.zoom({ position: 'topright' }).addTo(this.map);
    L.control.layers(layers, null, { position: 'topright' }).addTo(this.map);

    // Search location (search bar) or select point in the map
    this.map.on('geosearch/showlocation', e => this.searchPoint(e));
    this.map.on('click', e => this.selectPoint(e));

  }

  @HostListener('window:popstate', ['$event'])
  dismissModal() {
    // Close modal when back button selected
    this.modalController.dismiss();
  }

  searchPoint(result: any) {
    this.setMarker(result.location.y, result.location.x, result.location.label);
  }

  selectPoint(result: any) {
    this.routeService.getAddressFromCoordinates(result.latlng.lat, result.latlng.lng).subscribe(
      (response: any) => {
        let name = response.features[0].properties.label;
        let longitude = response.features[0].geometry.coordinates[0];
        let latitude = response.features[0].geometry.coordinates[1];
        this.setMarker(latitude, longitude, name);
        this.searchControl.searchElement.input.value = name;
      },
      _ => {
        let name = result.latlng.lat + ', ' + result.latlng.lng;
        this.setMarker(result.latlng.lat, result.latlng.lng, name);
      }
    );
  }

  searchCurrentLocation() {
    // Get actual location of user
    this.geolocation.getCurrentPosition().then(position => {
      this.setMarker(position.coords.latitude, position.coords.longitude, 'Tu ubicación');
    }).catch(error => {
      this.sharedService.showToast('No se ha podido obtener la localización', 3000);
      console.log(error);
    });
  }

  setMarker(latitude: number, longitude: number, name: string) {
    // Update the position of the marker
    this.pointEmpty = false;
    this.point = { latitude: latitude, longitude: longitude, name: name };
    if (this.marker) {
      this.map.removeLayer(this.marker);
    }
    this.marker = L.marker([latitude, longitude]);
    this.marker.addTo(this.map);
    this.map.setView([latitude, longitude], 18);
  }

  onSelect() {
    if (this.pointEmpty) {
      this.sharedService.showToast('Selecciona una localización', 3000);
      return;
    }

    this.modalController.dismiss({
      'point': this.point
    });
  }

}
