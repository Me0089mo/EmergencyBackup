import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login-card',
  templateUrl: './login-card.component.html',
  styleUrls: ['./login-card.component.scss'],
})
export class LoginCardComponent implements OnInit {
  @Output('token') user_token = new EventEmitter<string>();
  public username: string = 'asdf1234damian@gmail.com';
  public password: string = 'passw0rd';
  public showSpinner: boolean = false;
  public displayError: boolean = false;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit(): void {}

  login(): void {
    this.showSpinner = true;
    this.displayError = false;
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
          this.user_token.emit(res);
          this.showSpinner = false;
        },
        error: (err) => {
          this.showSpinner = false;
        },
        complete: () => {},
      });
  }
}
