import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-index',
  templateUrl: './index.page.html',
  styleUrls: ['./index.page.scss'],
})
export class IndexPage implements OnInit {

  public id: string;

  constructor(private activatedRoute: ActivatedRoute) { 
  }

  ngOnInit() {
    this.id = this.activatedRoute.snapshot.paramMap.get("id");
  }

}
