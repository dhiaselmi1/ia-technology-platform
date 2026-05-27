import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
export class NewsComponent implements OnInit {
  private api = inject(ApiService);

  news: any[] = [];
  showDialog = false;
  editingId: number | null = null;
  form = { title: '', content: '', imageUrl: '', featured: false };

  uploading = false;
  uploadPreview: string | null = null;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.get<any[]>('news').subscribe(r => this.news = r || []);
  }

  openNew(): void {
    this.editingId = null;
    this.form = { title: '', content: '', imageUrl: '', featured: false };
    this.uploadPreview = null;
    this.showDialog = true;
  }

  edit(item: any): void {
    this.editingId = item.id;
    this.form = { title: item.title, content: item.content || '', imageUrl: item.imageUrl || '', featured: item.featured || false };
    this.uploadPreview = item.imageUrl ? 'http://localhost:8080/uploads/' + item.imageUrl : null;
    this.showDialog = true;
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];

    const reader = new FileReader();
    reader.onload = () => this.uploadPreview = reader.result as string;
    reader.readAsDataURL(file);

    this.uploading = true;
    const fd = new FormData();
    fd.append('file', file);
    this.api.upload<{ path: string }>('files/images', fd).subscribe({
      next: (res) => {
        this.form.imageUrl = res.path;
        this.uploading = false;
      },
      error: () => {
        this.uploading = false;
        this.uploadPreview = null;
      }
    });
  }

  removeImage(): void {
    this.form.imageUrl = '';
    this.uploadPreview = null;
  }

  save(): void {
    const req = this.editingId
      ? this.api.put(`news/${this.editingId}`, this.form)
      : this.api.post('news', this.form);
    req.subscribe({ next: () => { this.showDialog = false; this.load(); } });
  }

  delete(id: number): void {
    if (confirm('Supprimer cette actualité ?')) {
      this.api.delete(`news/${id}`).subscribe(() => this.load());
    }
  }

  toggleFeatured(item: any): void {
    item.featured = !item.featured;
    this.api.put(`news/${item.id}`, { title: item.title, content: item.content, imageUrl: item.imageUrl, featured: item.featured }).subscribe();
  }
}
