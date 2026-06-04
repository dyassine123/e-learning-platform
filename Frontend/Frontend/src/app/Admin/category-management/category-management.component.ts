import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../services/category.service';
import { CategoryResponse } from '../../dtos/category.dto';

@Component({
  selector: 'app-category-management',
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.css']
})
export class CategoryManagementComponent implements OnInit {
  categories: CategoryResponse[] = [];
  loading = false;
  
  // Form state
  showForm = false;
  editMode = false;
  selectedId?: number;
  
  formData = {
    name: '',
    description: ''
  };

  message = '';
  error = '';

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.list(0, 100).subscribe({
      next: (res) => {
        this.categories = res.content;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load categories.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editMode = false;
    this.showForm = true;
    this.formData = { name: '', description: '' };
    this.clearMessages();
  }

  openEdit(cat: CategoryResponse): void {
    this.editMode = true;
    this.showForm = true;
    this.selectedId = cat.id;
    this.formData = { name: cat.name, description: cat.description || '' };
    this.clearMessages();
  }

  onSubmit(): void {
    if (!this.formData.name.trim()) {
      this.error = 'Name is required.';
      return;
    }

    if (this.editMode && this.selectedId) {
      this.categoryService.update(this.selectedId, this.formData).subscribe({
        next: () => {
          this.message = 'Category updated successfully.';
          this.showForm = false;
          this.loadCategories();
        },
        error: (err) => this.error = err?.error?.message || 'Update failed.'
      });
    } else {
      this.categoryService.create(this.formData).subscribe({
        next: () => {
          this.message = 'Category created successfully.';
          this.showForm = false;
          this.loadCategories();
        },
        error: (err) => this.error = err?.error?.message || 'Creation failed.'
      });
    }
  }

  deleteCategory(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoryService.delete(id).subscribe({
        next: () => {
          this.message = 'Category deleted.';
          this.loadCategories();
        },
        error: (err) => this.error = err?.error?.message || 'Deletion failed.'
      });
    }
  }

  clearMessages(): void {
    this.message = '';
    this.error = '';
  }
}
