import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { LoginCardComponent } from './components/login-card/login-card.component';
import { BackupsModalComponent } from './components/backups-modal/backups-modal.component';

interface file_arr_interface {
  files: string[];
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  private user_token: string = '';
  public logedIn: boolean = false;
  public files_available: string[] = [];
  constructor(
    public snackBar: MatSnackBar,
    private http: HttpClient,
    private modalService: NgbModal
  ) {
    this.displayModal();
  }

  handleError() {}

  displayModal() {
    let login_modal_ref = this.modalService.open(LoginCardComponent, {
      centered: true,
    });
    login_modal_ref.componentInstance.user_token.subscribe({
      next: (res: string) => {
        this.user_token = res;
        login_modal_ref.close();
        this.checkForBackup();
      },
    });
  }

  checkForBackup() {
    this.http
      .get(environment.host_url + environment.api_check_bu, {
        headers: {
          'Content-Type': 'application/json',
          authorization: this.user_token,
        },
      })
      .subscribe({
        next: (res) => {
          if (res) {
            this.display_backup();
          } else {
            this.display_no_backup();
          }
        },
        error: (err) => {
          this.display_error();
        },
      });
  }

  display_backup() {
    this.http
      .get(environment.host_url + environment.api_download, {
        headers: {
          'Content-Type': 'application/json',
          authorization: this.user_token,
        },
      })
      .subscribe({
        next: (res: any) => {
          let login_modal_ref = this.modalService.open(BackupsModalComponent, {
            centered: true,
          });
          login_modal_ref.componentInstance.file_name_arr = res['files'];
          login_modal_ref.componentInstance.user_token = this.user_token;
        },
      });
  }

  display_no_backup() {}
  display_error() {}

  // getScreenSize() {
  //   this.screenSize[0] = window.screen.width;
  //   this.screenSize[1] = window.screen.height;
  // }

  // openBrowserWarning() {
  //   let snackBarRef2 = this.snackBar.open(
  //     'Para restablecer tu respaldo directo del celular debes ingresar a la App móvil o descarga el respaldo desde tu computadora',
  //     'OK',
  //     { duration: 5000 }
  //   );
  // }

  // downLoadFile(data: any, type: string) {}
}
