import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-user-comments',
  templateUrl: './user-comments.page.html',
  styleUrls: ['./user-comments.page.scss'],
})
export class UserCommentsPage implements OnInit {

  public id: string;

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.id = this.activatedRoute.snapshot.paramMap.get("id");
  }

}
