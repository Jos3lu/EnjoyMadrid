import { Component, OnInit } from '@angular/core';
import { SafeHtml } from '@angular/platform-browser';
import { SharedService } from 'src/app/services/shared/shared.service';

@Component({
  selector: 'app-store-places',
  templateUrl: './store-places.page.html',
  styleUrls: ['./store-places.page.scss'],
})
export class StorePlacesPage implements OnInit {

  constructor(
    private sharedService: SharedService
  ) { }

  ngOnInit() {
  }

  async onError(event: any) {
    // Reload image if error loading it
    this.sharedService.reloadImage(event, 'data-retry', 'data-max-retry', 'assets/flag.png');
  }

  sanitizeHtml(innerHTMl: string): SafeHtml {
    // Sanitize html
    return this.sharedService.sanitizeHtml(innerHTMl);
  }

}
