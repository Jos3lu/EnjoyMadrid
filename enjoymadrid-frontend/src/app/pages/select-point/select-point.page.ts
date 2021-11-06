import { Component, Input, OnInit } from '@angular/core';
import * as Leaflet from 'leaflet';
import { RouteModel } from 'src/app/models/route.model';

@Component({
  selector: 'app-select-point',
  templateUrl: './select-point.page.html',
  styleUrls: ['./select-point.page.scss'],
})
export class SelectPointPage implements OnInit {

  // Data passed in by componentProps
  @Input() route: RouteModel;

  map: Leaflet.Map;

  constructor() { }

  ngOnInit() {
  }

  ionViewDidEnter() {
  
    const standard = Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Imagery &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    });

    const satellite = Leaflet.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Imagery &copy; Esri'
    });

    const layers = {
      "Satélite": satellite,
      "Predeterminado": standard
    };

    this.map = Leaflet.map('map', {
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    Leaflet.control.layers(layers).addTo(this.map);

  }

}
