import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserModel } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { UserService } from 'src/app/services/user/user.service';

@Component({
  selector: 'app-update-user',
  templateUrl: './update-user.page.html',
  styleUrls: ['./update-user.page.scss'],
})
export class UpdateUserPage implements OnInit {

  user: UserModel;
  imageUser: File;

  // Show/hide passwords
  showPasswordCurrent: boolean;
  showPasswordNew: boolean;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private sharedService: SharedService,
    private router: Router
  ) {
    this.showPasswordCurrent;
    this.showPasswordNew;
  }

  ngOnInit() {
    this.user = { id: -1, name: '', username: '', oldPassword: '', password: '' };
  }

  ionViewWillEnter() {
    this.user = Object.assign(this.user, this.authService.getUserAuth());
  }

  togglePasswordCurrent() {
    this.showPasswordCurrent = !this.showPasswordCurrent;
  }

  togglePasswordNew() {
    this.showPasswordNew = !this.showPasswordNew;
  }

  loadProfilePicture(event: { target: { files: File[]; }; }) {
    this.imageUser = event.target.files[0];
    if (this.imageUser) {
      let pictureForm = new FormData();
      pictureForm.append("imageUser", this.imageUser);
      this.userService.updateUserPictureProfile(this.user.id, pictureForm).subscribe(
        user => {
          this.user.photo = user.photo;
          this.authService.getUserAuth().photo = user.photo;
        },
        error => this.sharedService.onError(error, 5000)
      );
    }
  }

  async onSubmit() {
    // First request update profile picture then rest of the user
    this.userService.updateUser(this.user.id, this.user).subscribe(
      user => {
        if (this.user.password) {
          this.authService.signOut().subscribe(
            _ => {
              this.authService.signIn({ username: user.username, password: this.user.password }).subscribe(
                _ => {
                  this.user.password = '';
                  this.user.oldPassword = '';
                }
              );
            }
          );
        } else {
          this.authService.getUserAuth().name = user.name;
        }
        this.router.navigateByUrl('/');
      },
      error => this.sharedService.onError(error, 5000));
  }
}
