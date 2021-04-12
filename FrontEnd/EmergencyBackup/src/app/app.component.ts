import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  private user_token: any = null;
  title = 'EmergencyBackup';
  username = 'asdf1234damian@gmail.com';
  password = 'passw0rd';
  showSpinner = false;
  constructor(public snackBar: MatSnackBar, private http: HttpClient) {}

  handleError() {}

  login() {
    this.showSpinner = true;
    this.http
      .post(
        environment.host_url + environment.api_login,
        {
          email: this.username,
          password: this.password,
        },
        {
          headers: { 'Content-Type': 'application/json' },
          responseType: 'text',
        }
      )
      .subscribe({
        next: (res) => {
          this.user_token = res;
          console.log(res);
          this.download();
          this.showSpinner = false;
        },
        error: (err) => {
          this.openErrorDialog();
          this.showSpinner = false;
        },
        complete: () => {},
      });
  }

  openErrorDialog() {
    let snackBarRef = this.snackBar.open(
      'Usuario o contraseña incorrectos',
      'Ok',
      { duration: 3000 }
    );
  }

  download() {
    this.http
      .get(environment.host_url + environment.api_download, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: this.user_token,
        },
        responseType: 'arraybuffer',
      })
      .subscribe({
        next: (res) => {
          let blob = new Blob([res], { type: 'text/csv' });
          let url = window.URL.createObjectURL(blob);
          let pwa = window.open(url);
          if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
            alert('Please disable your Pop-up blocker and try again.');
          }
          console.log(res);
        },
      });
  }
  downLoadFile(data: any, type: string) {}
}
