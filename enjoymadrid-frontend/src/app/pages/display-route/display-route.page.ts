import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { Platform } from '@ionic/angular';
import { trigger, style, animate, transition } from '@angular/animations';
import { RouteModel } from 'src/app/models/route.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import { Router } from '@angular/router';
import { LineString, MultiLineString } from 'geojson';

@Component({
  selector: 'app-display-route',
  templateUrl: './display-route.page.html',
  styleUrls: ['./display-route.page.scss'],
  animations: [
    trigger('openClose', [
      transition(':enter', [
        style({transform: 'translateX(-100%)'}),
        animate('250ms ease-in', style({transform: 'translateX(0%)'}))
      ]),
      transition(':leave', [
        animate('250ms ease-in', style({transform: 'translateX(-100%)'}))
      ])
    ])
  ]
})
export class DisplayRoutePage implements OnInit {

  // Get if running on Desktop or mobile
  showSideMenu: boolean;
  // Map
  map: L.Map;
  // Close side menu when user click button
  isOpen: boolean;
  // Route to view in the map
  route: RouteModel
  // Map with the type 
  iconTransport: Map<string, string>;
  // Map with line zones associated to general line
  linesZone: Map<string, string>;
  // Map with the color of the different lines of subway, commuter & bus
  colorSegments: Map<string, string>;
  // Store the segment color, the icon to display & if line is solid or dotted
  segmentsVisual: any[];

  constructor(
    private platform: Platform,
    private sharedService: SharedService,
    private router: Router
    ) 
  { }

  ngOnInit() {
    this.platform.ready().then(() => {
      this.showSideMenu = this.platform.is('desktop');
    });

    // Different icon for the mode of transport
    this.iconTransport = new Map<string, string>([ ['A pie', 'walk.png'], ['Metro', 'subway.png'], ['Bus', 'bus.png'],
      ['BiciMAD', 'bicycle.png'], ['Cercanías', 'commuter.png'] ]);
    // Different line zone to line
    this.linesZone = new Map<string, string>([ ["6-1", "6"], ["6-2", "6"], ["7a", "7"], ["7b", "7"], 
      ["9A", "9"], ["9B", "9"], ["12-1", "12"], ["12-2", "12"] ]);
    // Line -> color segment
    this.colorSegments = new Map<string, string>([ ["1", "#2DBEF0"], ["2", "#ED1C24"], ["3", "#FFD000"], 
      ["4", "#B65518"], ["5", "#8FD400"], ["6", "#98989B"], ["7", "#EE7518"], ["8", "#EC82B1"], 
      ["9", "#A60084"], ["10", "#005AA9"], ["11", "#009B3A"], ["12", "#A49800"], ["R", "#D5D2CD"],
      ["C-1", "#4FB0E5"], ["C-2", "#008B45"], ["C-3", "#9F2E86"], ["C-3a", "#EA65A8"], ["C-4a", "#005AA3"], ["C-4b", "#005AA3"], 
      ["C-5", "F9BA13#"], ["C-7", "#ED1C24"], ["C-8", "#008B45"], ["C-9", "#F95900"], ["C-10", "#90B70E"], 
      ["Bus", "#0178BC"], ["BiciMAD","#FFAD00"], ["A pie","#2D2E2D"] ]);
    // Init segmentsVisual
    this.segmentsVisual = [];

    this.route = this.sharedService.getRoute();
    this.sharedService.setRoute(undefined);
    if (!this.route) {
      this.route = {
        id: 8514,
        name: "Ruta1",
        origin: {id: 8516,name: "40.44638865497956, -3.6090087890625004",longitude: -3.6090087890625004,latitude: 40.44638865497956},
        destination: {id: 8515,name: "40.409518722412905, -3.672866821289063",longitude: -3.672866821289063,latitude: 40.409518722412905},
        maxDistance: 1,
        transports: ["Metro","Bus","Cercanías","BiciMAD"],
        preferences: {"C_Instalaciones culturales": 4,"C_Parques y jardines": 5,"C_Escuelas de cocina y catas de vinos y aceites": 0,"C_Empresas de guías Turísticas": 0,"C_Edificios y monumentos": 3,"C_Centros de ocio": 0,"D_Deportes": 2,"T_Tiendas": 3,"R_Restauración": 2},
        date: new Date("2022-02-04"),
        points: [{id: 8516,name: "40.44638865497956, -3.6090087890625004",longitude: -3.6090087890625004,latitude: 40.44638865497956},{id: 5004,name: "Nectar-av.canillejas",longitude: -3.615277172363707,latitude: 40.44122432803461},{id: 5005,name: "Pza.cronos",longitude: -3.619036577532808,latitude: 40.43971780753817},{id: 3840,name: "Suanzes",longitude: -3.62683974417714,latitude: 40.4408512430777},{id: 3400,name: "Ciudad lineal",longitude: -3.6381575883455,latitude: 40.4380484084201},{id: 3297,name: "Pueblo nuevo",longitude: -3.64282293725217,latitude: 40.4356585154991},{id: 3842,name: "Quintana",longitude: -3.64736224209832,latitude: 40.4335849416495},{id: 3843,name: "El carmen",longitude: -3.65757254725966,latitude: 40.4318932920946},{id: 3360,name: "Ventas",longitude: -3.66360584172735,latitude: 40.4308848031949},{id: 3276,name: "Manuel becerra",longitude: -3.66920518071164,latitude: 40.4279044798375},{id: 3186,name: "O'donnell",longitude: -3.66859514629195,latitude: 40.4228884246446},{id: 3677,name: "Sainz de baranda",longitude: -3.66951374370264,latitude: 40.4150284968071},{id: 8515,name: "40.409518722412905, -3.672866821289063",longitude: -3.672866821289063,latitude: 40.409518722412905}],
        segments: [
          {id: 8517,source: 0,target: 1,distance: 0.9,duration: 11,transportMode: "A pie",steps: {"26-26": "Llegar a su destino, a la izquierda","0-2": "Camina hacia el sur en Calle de San Faustino","23-24": "¡Gire a la derecha a Plaza Manuel Escobar!","11-23": "¡Gire ligeramente a la izquierda a Calle del Néctar!","24-26": "¡Gire a la izquierda!","2-3": "¡Siga todo recto a Calle de San Faustino!","3-8": "¡Siga todo recto a Calle de San Faustino!","8-11": "¡Gire a la derecha a Calle del Néctar!"},polyline: [[40.446389,-3.608935],[40.446127,-3.608931],[40.445981,-3.608929],[40.445657,-3.608924],[40.444764,-3.608923],[40.444701,-3.60893],[40.444474,-3.608968],[40.4443,-3.60896],[40.444119,-3.608963],[40.444017,-3.609265],[40.443943,-3.609479],[40.443897,-3.609612],[40.443791,-3.60971],[40.443593,-3.609988],[40.443333,-3.610359],[40.442694,-3.611245],[40.442238,-3.611879],[40.442167,-3.612123],[40.442158,-3.612183],[40.442152,-3.612289],[40.442116,-3.612387],[40.441738,-3.613536],[40.441692,-3.613675],[40.441642,-3.613829],[40.441692,-3.61385],[40.441479,-3.614527],[40.441237,-3.615284]],line: null},
          {id: 8518,source: 1,target: 2,distance: null,duration: null,transportMode: "Bus",steps: {},polyline: [[40.441186,-3.615256],[40.441165,-3.615319],[40.44113,-3.615428],[40.441109,-3.615512],[40.441091,-3.615568],[40.441044,-3.615713],[40.440833,-3.616342],[40.440812,-3.616405],[40.440555,-3.617163],[40.440525,-3.617253],[40.440238,-3.618127],[40.440147,-3.618385],[40.440093,-3.618535],[40.440141,-3.618563],[40.440181,-3.618607],[40.44021,-3.618664],[40.440226,-3.618735],[40.440225,-3.618809],[40.440173,-3.618937],[40.440128,-3.61898],[40.440018,-3.619003],[40.439965,-3.618981],[40.439926,-3.618946],[40.439894,-3.618899],[40.439873,-3.618843],[40.439766,-3.618901],[40.439726,-3.618922],[40.439691,-3.618939]],line: "28"},
          {id: 8519,source: 2,target: 3,distance: 1.05,duration: 13,transportMode: "A pie",steps: {"6-7": "¡Gire a la izquierda a Calle de Albasanz!","7-8": "¡Gire a la derecha!","17-19": "¡Gire a la derecha a Calle de San Romualdo!","43-47": "¡Gire a la izquierda!","11-13": "¡Gire a la derecha!","19-34": "¡Gire a la izquierda!","34-43": "¡Gire a la izquierda!","51-51": "Llegar a su destino, a la izquierda","8-11": "¡Gire a la izquierda!","50-51": "¡Gire a la izquierda!","48-50": "¡Gire a la derecha!","0-2": "Camina hacia el norte en Calle de Cronos","47-48": "¡Gire a la derecha!","2-6": "¡Gire a la izquierda!","13-17": "¡Gire bastante a la derecha a Calle de San Romualdo!"},polyline: [[40.439691,-3.618939],[40.439726,-3.618922],[40.439766,-3.618901],[40.439784,-3.618947],[40.439832,-3.619028],[40.439862,-3.619062],[40.439921,-3.619108],[40.439833,-3.619376],[40.439898,-3.619409],[40.439802,-3.619691],[40.439622,-3.620224],[40.439168,-3.621587],[40.439237,-3.621712],[40.439185,-3.621763],[40.440104,-3.622223],[40.441021,-3.622798],[40.441106,-3.62296],[40.441124,-3.623046],[40.441342,-3.623154],[40.441485,-3.623186],[40.441478,-3.623239],[40.441471,-3.623294],[40.441454,-3.623347],[40.441439,-3.623396],[40.441421,-3.62345],[40.441404,-3.623503],[40.441604,-3.623631],[40.441638,-3.623686],[40.441645,-3.623751],[40.441632,-3.623831],[40.441652,-3.62384],[40.441686,-3.623855],[40.441761,-3.623889],[40.441805,-3.62391],[40.441828,-3.62392],[40.441799,-3.624018],[40.441373,-3.625586],[40.441175,-3.626302],[40.441055,-3.626711],[40.441015,-3.626822],[40.44099,-3.626913],[40.440951,-3.627052],[40.440879,-3.627016],[40.440874,-3.627014],[40.440915,-3.626871],[40.440938,-3.626789],[40.440993,-3.626588],[40.441007,-3.62654],[40.440925,-3.626501],[40.440889,-3.626627],[40.440941,-3.626653],[40.440885,-3.626856]],line: null},
          {id: 8520,source: 3,target: 8,distance: null,duration: null,transportMode: "Metro",steps: {},polyline: [[40.4408512430777,-3.62683974417714],[40.4380484084201,-3.6381575883455],[40.4356585154991,-3.64282293725217],[40.4335849416495,-3.64736224209832],[40.4318932920946,-3.65757254725966],[40.4308848031949,-3.66360584172735]],line: "5"},
          {id: 8521,source: 8,target: 9,distance: null,duration: null,transportMode: "Metro",steps: {},polyline: [[40.4308848031949,-3.66360584172735],[40.4279044798375,-3.66920518071164]],line: "2"},
          {id: 8522,source: 9,target: 11,distance: null,duration: null,transportMode: "Metro",steps: {},polyline: [[40.4279044798375,-3.66920518071164],[40.4228884246446,-3.66859514629195],[40.4150284968071,-3.66951374370264]],line: "6-2"},
          {id: 8523,source: 11,target: 12,distance: 0.88,duration: 11,transportMode: "A pie",steps: {"9-15": "¡Gire a la izquierda a Calle de Jesús Aprendiz!","8-9": "¡Gire a la derecha a Calle de Samaria!","15-16": "¡Gire a la izquierda a Avenida de Nazaret!","20-24": "¡Gire a la izquierda a Calle de Jesús Aprendiz!","33-33": "Llegar a Calle Juan de Jáuregui, a la derecha","19-20": "¡Gire a la derecha a Avenida de Nazaret!","0-1": "Camina hacia el sur en Calle del Doctor Esquerdo","30-33": "¡Gire a la derecha a Calle Juan de Jáuregui!","24-25": "¡Gire a la derecha a Calle de Arias Montano!","1-4": "¡Gire a la derecha a Calle del Doctor Esquerdo!","16-19": "¡Gire a la derecha a Calle de Jesús Aprendiz!","25-30": "¡Gire a la izquierda a Calle de Antonio Díaz-Cañabate!","4-8": "¡Gire a la izquierda!"},polyline: [[40.415033,-3.669605],[40.414409,-3.669664],[40.414415,-3.669773],[40.414419,-3.669861],[40.414456,-3.67059],[40.414394,-3.670593],[40.413543,-3.670657],[40.413269,-3.670677],[40.412924,-3.670699],[40.412928,-3.670807],[40.41236,-3.670839],[40.412251,-3.670903],[40.41212,-3.67099],[40.41201,-3.671029],[40.411568,-3.671053],[40.411478,-3.671057],[40.411478,-3.671031],[40.411448,-3.671012],[40.41141,-3.671017],[40.411386,-3.671042],[40.411387,-3.671065],[40.411321,-3.671071],[40.410905,-3.671106],[40.409965,-3.671187],[40.409864,-3.671195],[40.40991,-3.671604],[40.409866,-3.671618],[40.409713,-3.671664],[40.40956,-3.671718],[40.409401,-3.671844],[40.409235,-3.671976],[40.409325,-3.672418],[40.409426,-3.672866],[40.409442,-3.672912]],line: null}]
      };
      //this.sharedService.showToast('No se ha podido obtener la ruta', 3000);
      //this.router.navigate(['/']);
    }

    // Iterate over the segments of the route
    this.route.segments.forEach(segment => {
      // Transform some lines to unify zones in one line
      if (this.linesZone.has(segment.line)) {
        segment.line = this.linesZone.get(segment.line);
      }

      // Get the color for the segment
      let colorSegment = segment.line && segment.transportMode !== 'Bus' 
      ? this.colorSegments.get(segment.line) 
      : this.colorSegments.get(segment.transportMode);
      // Dash pattern for the segment (for walk segments)
      let dashedSegment = segment.transportMode === 'A pie';
      // Icon (mode of transport) for the segment
      let iconSegment = this.iconTransport.get(segment.transportMode);

      // Store some features of the segments
      this.segmentsVisual.push({ color: colorSegment, dashed: dashedSegment, icon: iconSegment });
    });
  }

  ionViewDidEnter() {
    this.isOpen = true;

    const standard = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    });

    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; Esri'
    });

    const layers = {
      "<span style='cursor: pointer'>Satélite</span>": satellite,
      "<span style='cursor: pointer'>Predeterminado</span>": standard
    };

    this.map = L.map('map', {
      zoomControl: false,
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    L.control.zoom({ position: 'topright' }).addTo(this.map);
    L.control.layers(layers, null, { position: 'topright' }).addTo(this.map);


    let multiPolyline: L.Polyline<LineString | MultiLineString, any>;
    let coordinates = [];
    // Iterate over the segments of the route
    this.route.segments.forEach((segment, index) => {
      // Transform number[][] to LatLng array in polyline
      let coords = segment.polyline.map(coord => new L.LatLng(coord[0], coord[1]));
      
      // Establish the color for the segment
      let color = this.segmentsVisual[index].color;
      // Dash pattern for the segment (for walk segments)
      let dashArray = this.segmentsVisual[index].dashed ? '1 7' : null;
      
      // Tooltip set line if applicable
      let lineTooltip = segment.line ? 
      '<div style="margin-top: 3px;"><span style="border-radius: 4px; padding: 2px 5px; background-color: '
      + color
      + '; color: #ffffff;">'
      + segment.line
      + '</span></div>' : '';
      // Tooltip for the polylines that make up the route
      let tooltip = 
      '<div style="text-align: center;"><img style="height: 20px; width: 20px; margin: 0px auto; display:block;" src="./assets/' 
      + this.segmentsVisual[index].icon + '">'
      + this.route.points[segment.source].name 
      + '<ion-icon style="vertical-align: middle; font-size: 10px; opacity: 0.7" name="chevron-forward-outline"></ion-icon>' 
      + this.route.points[segment.target].name 
      + lineTooltip
      + '</div>';

      // Add polyline to map
      L.polyline(coords, {
        stroke: true,
        color: color,
        weight: 5,
        lineJoin: 'round',
        lineCap: 'round',
        dashArray: dashArray
      }).bindTooltip(tooltip, {
        sticky: true
      }).addTo(this.map);

      // Add the coordinates of each segment
      coordinates.push.apply(coordinates, coords);

      // Icon for marker
      let icon = L.icon({
        iconUrl: './assets/' + this.segmentsVisual[index].icon,
        iconSize: [20, 20]
      });

      // Add markers to map
      new L.Marker([this.route.points[segment.source].latitude, this.route.points[segment.source].longitude], {
        icon: icon
      }).addTo(this.map).bindTooltip(this.route.points[segment.source].name);
    });

    // Marker for destination point
    new L.Marker([this.route.destination.latitude, this.route.destination.longitude], {
      icon: L.icon({ iconUrl: './assets/destination.png', iconSize: [25, 25]})
    }).addTo(this.map).bindTooltip(this.route.destination.name);

    // Polyline of the whole route
    multiPolyline = L.polyline(coordinates);
    this.map.fitBounds(multiPolyline.getBounds(), { paddingTopLeft: [this.showSideMenu ? 100 : 0, 0] });
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

}
