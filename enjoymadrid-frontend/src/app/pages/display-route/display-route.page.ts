import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { Platform } from '@ionic/angular';
import { trigger, style, animate, transition } from '@angular/animations';
import { RouteModel } from 'src/app/models/route.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import { Router } from '@angular/router';

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

    this.route = this.sharedService.getRoute();
    this.sharedService.setRoute(undefined);
    if (!this.route) {
      this.route = {
        name: 'Ruta1',
        date: new Date(2022,2,1),
        preferences: {"C_Instalaciones culturales": 3, "C_Parques y jardines": 4, "C_Escuelas de cocina y catas de vinos y aceites": 0, "C_Empresas de guías Turísticas": 0, "C_Edificios y monumentos": 3, "C_Centros de ocio": 0, "T_Deporte": 0, "T_Tiendas": 1, "R_Restauración": 2},
        maxDistance: 1.1,
        transports: ["Metro", "Bus", "BiciMAD", "Cercanías"],
        origin: {
          id: 8515,
          name: "40.42162113274221, -3.6025714874267583",
          latitude: 40.42162113274221,
          longitude: -3.6025714874267583
        },
        destination: {
          id: 8516,
          name: "Museo del Prado, 23, Calle Ruiz de Alarcón, Jerónimos, Retiro, Madrid, Área metropolitana de Madrid y Corredor del Henares, Comunidad de Madrid, 28014, España",
          latitude: 40.41379255,
          longitude: -3.6920378829351304
        },
        points: [{id: 8528,name: "161 A, Calle de Sofía, Rosas, San Blas - Canillejas, Madrid, Área metropolitana de Madrid y Corredor del Henares, Comunidad de Madrid, 28001, España",longitude: -3.603243058874388,latitude: 40.42162525},{id: 7876,name: "Albericia-av.guadalajara",longitude: -3.612667967424895,latitude: 40.42264339309563},{id: 7820,name: "Arcos de jalón-albericia",longitude: -3.61520900638287,latitude: 40.424942425521266},{id: 4941,name: "Pobladura valle-san roman valle",longitude: -3.62324599600952,latitude: 40.42360884264311},{id: 4942,name: "Pobladura valle-arcos jalon",longitude: -3.625406664085768,latitude: 40.42269369980248},{id: 8193,name: "Castillo ucles-castillo simancas",longitude: -3.62311936906799,latitude: 40.43017307797765},{id: 6250,name: "Castillo ucles nº 49",longitude: -3.623000369495517,latitude: 40.43249182553872},{id: 5518,name: "Julian camarillo-albarracin",longitude: -3.630976361994831,latitude: 40.43418488755624},{id: 5519,name: "Julian camarillo-hnos.gª noblejas",longitude: -3.634044348526793,latitude: 40.43320416282766},{id: 4348,name: "Alcala-hermanos de pablo",longitude: -3.644025526331181,latitude: 40.435176145383764},{id: 8269,name: "Alcala-virgen del sagrario",longitude: -3.64796307730134,latitude: 40.43330488204536},{id: 3670,name: "José María Pereda",longitude: -3.6485306152875148,latitude: 40.43272086245887},{id: 3348,name: "Plaza de la Independencia",longitude: -3.688398,latitude: 40.419752},{id: 8529,name: "Museo del Prado, 23, Calle Ruiz de Alarcón, Jerónimos, Retiro, Madrid, Área metropolitana de Madrid y Corredor del Henares, Comunidad de Madrid, 28014, España",longitude: -3.6920378829351304,latitude: 40.41379255}],
        lines: {
          "1-3": "105 [2]",
          "4-5": "77 [2]",
          "6-9": "38 [2]",
          "12-13": "6-2 [1]",
          "13-14": "9A [2]"
        },
        segments: [
          {id: 8517,source: 0,target: 1,distance: 0.9205,duration: 11.046666666666665,transportMode: "A pie",steps: {"5-9": "¡Gire a la izquierda a Calle de San Venancio!","20-26": "¡Gire a la izquierda!","26-26": "Llegar a su destino, es recto","0-2": "Camina hacia el noroeste","11-13": "¡Gire a la derecha a Calle de Barbastro!","2-3": "¡Gire a la izquierda!","13-16": "¡Gire a la izquierda a Calle de Boltaña!","3-5": "¡Gire a la derecha a Calle de Mequinenza!","16-19": "¡Gire a la derecha a Calle de Torre Arias!","9-11": "¡Gire ligeramente a la izquierda a Calle de San Venancio!","19-20": "¡Gire a la izquierda!"},"polyline": [[40.446939,-3.605733],[40.447,-3.605766],[40.447148,-3.605848],[40.447116,-3.605991],[40.447151,-3.606016],[40.447205,-3.606068],[40.447273,-3.60732],[40.44726,-3.607417],[40.447226,-3.607668],[40.447242,-3.607843],[40.446742,-3.608941],[40.445825,-3.610369],[40.445851,-3.610449],[40.446578,-3.611154],[40.446517,-3.611262],[40.446218,-3.611977],[40.446167,-3.612101],[40.446406,-3.612375],[40.446507,-3.612491],[40.446614,-3.612609],[40.446588,-3.612654],[40.446547,-3.612618],[40.446518,-3.612629],[40.446127,-3.613236],[40.446095,-3.613304],[40.446071,-3.613338],[40.445506,-3.614194]] }, 
          {id: 8518,source: 1,target: 3,distance: 0.8463999999999999,duration: 1.2116666666666667,transportMode: "Bus",steps: {},"polyline": [[40.445437,-3.614118],[40.445379,-3.61421],[40.445308,-3.614318],[40.44499,-3.61482],[40.444638,-3.615356],[40.444437,-3.615661],[40.444146,-3.61612],[40.44398,-3.616392],[40.443876,-3.616569],[40.443868,-3.616585],[40.443725,-3.616867],[40.443693,-3.61695],[40.443603,-3.617171],[40.443427,-3.617666],[40.443283,-3.618174],[40.442939,-3.619384],[40.442717,-3.620198],[40.442698,-3.620267],[40.44266,-3.620416],[40.442023,-3.622786],[40.442,-3.622878]] }, 
          {id: 8519,source: 3,target: 4,distance: 0.7258,duration: 8.71,transportMode: "A pie",steps: {"16-22": "¡Gire a la derecha!","28-29": "¡Gire a la izquierda a Calle de Alegría de Oria!","0-3": "Camina hacia el oeste","3-16": "¡Gire ligeramente a la izquierda!","29-30": "¡Gire a la derecha a Calle de Alcalá!","30-30": "Llegar a Calle de Alcalá, a la derecha","22-28": "¡Gire a la izquierda!"},"polyline": [[40.442101,-3.622922],[40.442081,-3.622993],[40.441995,-3.6233],[40.441998,-3.623316],[40.441995,-3.623327],[40.441987,-3.623354],[40.441962,-3.623443],[40.441901,-3.623663],[40.441883,-3.623729],[40.441864,-3.623796],[40.441849,-3.623852],[40.441828,-3.62392],[40.441799,-3.624018],[40.441373,-3.625586],[40.441175,-3.626302],[40.441055,-3.626711],[40.441015,-3.626822],[40.441032,-3.626831],[40.440958,-3.627108],[40.440899,-3.627328],[40.440712,-3.628015],[40.44069,-3.628089],[40.440701,-3.628101],[40.440681,-3.628177],[40.440414,-3.629137],[40.4404,-3.629195],[40.440164,-3.630103],[40.440148,-3.630165],[40.440007,-3.630689],[40.439927,-3.630656],[40.439881,-3.630824]] }, 
          {id: 8520,source: 4,target: 5,distance: 0.1876,duration: 0.25,transportMode: "Bus",steps: {},"polyline": [[40.439881,-3.630824],[40.439797,-3.631133],[40.439731,-3.631382],[40.439626,-3.631779],[40.439586,-3.631929],[40.439516,-3.632185],[40.439486,-3.632292],[40.439471,-3.632348],[40.43932,-3.632916]] }, 
          {id: 8521,source: 5,target: 6,distance: 0.7437999999999999,duration: 8.925,transportMode: "A pie",steps: {"12-30": "¡Gire a la derecha a Calle de Alcalá!","0-2": "Camina hacia el oeste en Calle de Alcalá","11-12": "¡Gire a la izquierda a Calle del General Aranaz!","30-35": "¡Gire a la derecha!","2-3": "¡Gire a la derecha a Calle de Riobamba!","45-45": "Llegar a Calle de Alcalá, a la derecha","35-45": "¡Gire ligeramente a la derecha a Calle de Alcalá!","3-11": "¡Gire a la izquierda!"},"polyline": [[40.43932,-3.632916],[40.439319,-3.63292],[40.439226,-3.633237],[40.439292,-3.633266],[40.439279,-3.633316],[40.43908,-3.63402],[40.439069,-3.634076],[40.438978,-3.634407],[40.438813,-3.635003],[40.438693,-3.63544],[40.438727,-3.635467],[40.438697,-3.635559],[40.438608,-3.635495],[40.43859,-3.635563],[40.438466,-3.635998],[40.438412,-3.636078],[40.438239,-3.636726],[40.438165,-3.637006],[40.438094,-3.637274],[40.438051,-3.637435],[40.43797,-3.637737],[40.437924,-3.637912],[40.437905,-3.637974],[40.437888,-3.638033],[40.437866,-3.638097],[40.437834,-3.638186],[40.437774,-3.638354],[40.437728,-3.638482],[40.437685,-3.638602],[40.437664,-3.638655],[40.437648,-3.638693],[40.437657,-3.638739],[40.437664,-3.63885],[40.437618,-3.638947],[40.43759,-3.638967],[40.437513,-3.638991],[40.437489,-3.639042],[40.437453,-3.639117],[40.437292,-3.639463],[40.437192,-3.639665],[40.437098,-3.639857],[40.43706,-3.639935],[40.436977,-3.640107],[40.436914,-3.640237],[40.436869,-3.640331],[40.436746,-3.640585]] }, 
          {id: 8522,source: 6,target: 9,distance: 1.165,duration: 2.25,transportMode: "Bus",steps: {},"polyline": [[40.436746,-3.640585],[40.43643,-3.641239],[40.436379,-3.641345],[40.436292,-3.641536],[40.436258,-3.641606],[40.436231,-3.64166],[40.436177,-3.641773],[40.435989,-3.64216],[40.435873,-3.642398],[40.435678,-3.642791],[40.435399,-3.643376],[40.435258,-3.643669],[40.435193,-3.643804],[40.435111,-3.643971],[40.435067,-3.644062],[40.435013,-3.644173],[40.434957,-3.644287],[40.434717,-3.64478],[40.434643,-3.644924],[40.434579,-3.645068],[40.434415,-3.645421],[40.434336,-3.645575],[40.434039,-3.64619],[40.434015,-3.646246],[40.433969,-3.646342],[40.433937,-3.646408],[40.433772,-3.646738],[40.433627,-3.647029],[40.433595,-3.647098],[40.433539,-3.64721],[40.433494,-3.647304],[40.43322,-3.647895],[40.433206,-3.647926],[40.433145,-3.648102],[40.433117,-3.648239],[40.433102,-3.648351],[40.433064,-3.648579],[40.433051,-3.648664],[40.433029,-3.648818],[40.432986,-3.649116],[40.432977,-3.649178],[40.432939,-3.649443],[40.432817,-3.650306],[40.432811,-3.650352],[40.432798,-3.650439],[40.432759,-3.650706],[40.432635,-3.651561],[40.432621,-3.651664],[40.432605,-3.651779],[40.432551,-3.652158],[40.432538,-3.652249],[40.432515,-3.652398],[40.432506,-3.652466],[40.432452,-3.652883]] }, 
          {id: 8523,source: 9,target: 10,distance: 0.6013,duration: 7.215,transportMode: "A pie",steps: {"24-25": "¡Gire a la izquierda!","21-24": "¡Gire a la izquierda a Calle de Alcalá!","25-28": "¡Gire a la derecha!","0-21": "Camina hacia el oeste en Calle de Alcalá","31-32": "¡Gire a la izquierda!","32-32": "Llegar a su destino, es recto","28-31": "¡Gire a la derecha!"},"polyline": [[40.432452,-3.652883],[40.432426,-3.653082],[40.432397,-3.653277],[40.432381,-3.653392],[40.432277,-3.654148],[40.432264,-3.654247],[40.432206,-3.654686],[40.432163,-3.655009],[40.432132,-3.655247],[40.432115,-3.655375],[40.432077,-3.655656],[40.432067,-3.655726],[40.432049,-3.655885],[40.43203,-3.656019],[40.431932,-3.656759],[40.431911,-3.656926],[40.431905,-3.656963],[40.431843,-3.657441],[40.431839,-3.657464],[40.431819,-3.657624],[40.431799,-3.657762],[40.431728,-3.658315],[40.431642,-3.658672],[40.431598,-3.658888],[40.431571,-3.659051],[40.431503,-3.659033],[40.43149,-3.659113],[40.431466,-3.659139],[40.431418,-3.659145],[40.431412,-3.659232],[40.431361,-3.65927],[40.431305,-3.659313],[40.431195,-3.659055]] }, 
          {id: 8524,source: 10,target: 11,distance: 1.3489,duration: 4.05,transportMode: "BiciMAD",steps: {"7-9": "¡Gire a la izquierda!","0-1": "Camina hacia el noroeste","38-48": "¡Entre en la rotonda y tome la 1a salida a Calle del Marqués de Mondéjar!","34-38": "¡Gire bastante a la izquierda a Calle de Alejandro González!","20-34": "¡Siga todo recto a Calle de Alcalá!","1-4": "¡Gire a la derecha!","9-20": "¡Gire a la izquierda a Calle de Alcalá!","48-49": "¡Gire a la izquierda a Calle de Rufino Blanco!","56-56": "Llegar a Paseo del Marqués de Zafra, a la izquierda","55-56": "¡Gire a la derecha a Paseo del Marqués de Zafra!","4-7": "¡Gire a la izquierda!","49-55": "¡Siga todo recto a Calle de Rufino Blanco!"},"polyline": [[40.431195,-3.659055],[40.431305,-3.659313],[40.431361,-3.65927],[40.431412,-3.659232],[40.431418,-3.659145],[40.431466,-3.659139],[40.43149,-3.659113],[40.431503,-3.659033],[40.431571,-3.659051],[40.431643,-3.659071],[40.431624,-3.659274],[40.431604,-3.659485],[40.431578,-3.659703],[40.431539,-3.660023],[40.431534,-3.660089],[40.431503,-3.660345],[40.431494,-3.6604],[40.431425,-3.660903],[40.431414,-3.660989],[40.431402,-3.661076],[40.431371,-3.661298],[40.431313,-3.661708],[40.431307,-3.661745],[40.431265,-3.662028],[40.431243,-3.662223],[40.43121,-3.662421],[40.431165,-3.662544],[40.430899,-3.663087],[40.430713,-3.663329],[40.430705,-3.663345],[40.43067,-3.663426],[40.43061,-3.663544],[40.430538,-3.663693],[40.430427,-3.66392],[40.430281,-3.664217],[40.430196,-3.664046],[40.429818,-3.663275],[40.429772,-3.663168],[40.429384,-3.662393],[40.429336,-3.662414],[40.429235,-3.662414],[40.429197,-3.6624],[40.429168,-3.662437],[40.428772,-3.663253],[40.428445,-3.663909],[40.428434,-3.663948],[40.428383,-3.664061],[40.42819,-3.66446],[40.42742,-3.666066],[40.426912,-3.665623],[40.426703,-3.665455],[40.426304,-3.665121],[40.426066,-3.664916],[40.426006,-3.664931],[40.425958,-3.664945],[40.425927,-3.664959],[40.426029,-3.66529]] }, 
          {id: 8525,source: 11,target: 12,distance: 0.6287,duration: 7.543333333333334,transportMode: "A pie",steps: {"19-19": "Llegar a su destino, a la izquierda","17-19": "¡Gire a la izquierda!","0-1": "Camina hacia el oeste en Paseo del Marqués de Zafra","15-17": "¡Gire a la derecha a Calle de Jorge Juan!","1-3": "¡Gire bastante a la izquierda a Calle Lanuza!","3-4": "¡Gire a la derecha a Calle de Hermosilla!","10-15": "¡Gire a la izquierda a Calle Antonio Toledano!","8-10": "¡Gire a la derecha a Calle Peñascales!","4-8": "¡Gire a la izquierda a Calle del Porvenir!"},"polyline": [[40.426029,-3.66529],[40.426123,-3.665592],[40.42558,-3.665524],[40.425402,-3.665496],[40.425428,-3.666057],[40.4254,-3.666063],[40.425385,-3.666066],[40.424626,-3.666221],[40.424039,-3.666344],[40.424054,-3.666763],[40.424053,-3.666852],[40.423737,-3.666906],[40.423267,-3.666973],[40.422921,-3.667005],[40.422892,-3.667009],[40.422863,-3.667012],[40.422869,-3.667094],[40.422953,-3.668601],[40.422921,-3.668604],[40.422909,-3.668621]] }, 
          {id: 8526,source: 12,target: 14,distance: 1.7308326079756542,duration: 0.05769442026585514,transportMode: "Metro",steps: {},"polyline": [[40.4228884246446,-3.66859514629195],[40.4150284968071,-3.66951374370264],[40.4183912105898,-3.67857510552646]] }, 
          {id: 8527,source: 14,target: 15,distance: 0.8054,duration: 9.665,transportMode: "A pie",steps: {"6-14": "¡Gire ligeramente a la derecha!","23-26": "¡Gire a la derecha a Paseo Julio Romero de Torres!","31-31": "Llegar a su destino, a la izquierda","19-20": "¡Gire a la derecha a Paseo de Venezuela!","0-1": "Camina hacia el oeste","28-29": "¡Gire ligeramente a la izquierda!","22-23": "¡Gire a la izquierda!","26-28": "¡Gire a la derecha!","1-3": "¡Gire a la derecha!","14-19": "¡Gire a la izquierda a Paseo de Fernán Núñez!","20-22": "¡Siga todo recto a Paseo de Venezuela!","3-4": "¡Gire a la izquierda a Calle de Ibiza!","4-6": "¡Gire ligeramente a la izquierda a Avenida de Menéndez Pelayo!","29-31": "¡Gire ligeramente a la izquierda!"},"polyline": [[40.418396,-3.678575],[40.418397,-3.678595],[40.418432,-3.678603],[40.41846,-3.67861],[40.418435,-3.678754],[40.418369,-3.678845],[40.418267,-3.678926],[40.418245,-3.679028],[40.418233,-3.679097],[40.418103,-3.679627],[40.41798,-3.680134],[40.417953,-3.680251],[40.417929,-3.680362],[40.417915,-3.68043],[40.417906,-3.680471],[40.417736,-3.680406],[40.417497,-3.680369],[40.417155,-3.680377],[40.416611,-3.680172],[40.416258,-3.680077],[40.416213,-3.6803],[40.416015,-3.681267],[40.415824,-3.6822],[40.415686,-3.682154],[40.415375,-3.682507],[40.415302,-3.682515],[40.415233,-3.682495],[40.415235,-3.682676],[40.415204,-3.682806],[40.414868,-3.683144],[40.414491,-3.683255],[40.413994,-3.683266]] } 
        ]
      };
      //this.sharedService.showToast('No se ha podido obtener la ruta', 3000);
      //this.router.navigate(['/']);
    }
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
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

}
