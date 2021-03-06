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

  // User information
  user: UserModel;
  imageUser: File;

  // Show/hide passwords
  showPasswordCurrent: boolean;
  showPasswordNew: boolean;

  // If modify password is selected
  passwordSelected: boolean;

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
    // Copy user
    this.user = Object.assign(this.user, this.authService.getUserAuth());
  }

  togglePasswordCurrent() {
    this.showPasswordCurrent = !this.showPasswordCurrent;
  }

  togglePasswordNew() {
    this.showPasswordNew = !this.showPasswordNew;
  }

  loadProfilePicture(event: any) {
    // Update photo independently of other user's information
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
    // Update user's information, if password has been modified then log back in
    this.userService.updateUser(this.user.id, this.user).subscribe(
      user => {
        if (this.user.password) {
          this.authService.signOut().subscribe(
            _ => {
              this.authService.signIn({ username: user.username, password: this.user.password }).subscribe(
                _ => {
                  this.user.password = '';
                  this.user.oldPassword = '';
                  this.router.navigateByUrl('/');
                }
              );
            }
          );
        } else {
          this.authService.getUserAuth().name = user.name;
          this.router.navigateByUrl('/');
        }
      },
      error => this.sharedService.onError(error, 5000));
  }
}
