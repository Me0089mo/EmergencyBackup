import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit, Output } from '@angular/core';
import { environment } from 'src/environments/environment';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-backups-modal',
  templateUrl: './backups-modal.component.html',
  styleUrls: ['./backups-modal.component.scss'],
})
export class BackupsModalComponent implements OnInit {
  @Input('file_name_arr') file_name_arr: string[] = [];
  @Input('user_token') user_token: string = '';
  constructor(private http: HttpClient) {}
  ngOnInit(): void {}

  download() {
    this.http
      .get(`${environment.host_url + environment.api_download}`, {
        headers: {
          authorization: this.user_token,
          all: 'true',
        },
        responseType: 'arraybuffer',
      })
      .subscribe({
        next: (res: ArrayBuffer) => {
          console.log(res);
          var blob = new Blob([new Uint8Array(res, 0, res.byteLength)], {
            type: '	application/zip',
          });

          saveAs(blob, 'respaldo.zip');
        },
        error: (err) => {
          console.log(err);
        },
      });
  }
}
