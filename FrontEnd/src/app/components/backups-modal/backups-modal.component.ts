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

  download(filename: string) {
    this.http
      .get(`${environment.host_url + environment.api_download}/${filename}`, {
        headers: {
          'Content-Type': 'application/json',
          authorization: this.user_token,
        },
        responseType: 'arraybuffer',
      })
      .subscribe({
        next: (res) => {
          let blob = new Blob([res], { type: 'text/csv' });
          saveAs(blob, filename);
        },
        error: () => {},
      });
  }
}
