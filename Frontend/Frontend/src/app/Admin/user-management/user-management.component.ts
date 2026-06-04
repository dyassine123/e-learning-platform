import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../dtos/user.dto';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
  loading = false;
  currentFilter: 'ALL' | 'TEACHER' | 'STUDENT' = 'ALL';
  searchQuery = '';

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.loading = true;
    this.userService.list().subscribe({
      next: (data) => {
        // Filter out admins from the list
        this.users = data.filter(user => user.role !== 'ADMIN');
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to fetch users', err);
        this.loading = false;
      }
    });
  }

  get studentCount(): number {
    return this.users.filter(u => u.role === 'STUDENT' && u.enabled).length;
  }

  get teacherCount(): number {
    return this.users.filter(u => u.role === 'TEACHER' && u.enabled).length;
  }

  get flaggedCount(): number {
    return this.users.filter(u => !u.enabled).length;
  }

  applyFilters(): void {
    this.filteredUsers = this.users.filter(user => {
      const matchesRole = this.currentFilter === 'ALL' || user.role === this.currentFilter;
      const matchesSearch = user.fullName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                          user.email.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesRole && matchesSearch;
    });
  }

  setFilter(filter: 'ALL' | 'TEACHER' | 'STUDENT'): void {
    this.currentFilter = filter;
    this.applyFilters();
  }

  onSearch(event: any): void {
    this.searchQuery = event.target.value;
    this.applyFilters();
  }

  toggleUserStatus(user: UserResponse): void {
    this.userService.toggleStatus(user.id).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
          this.applyFilters();
        }
      },
      error: (err) => {
        console.error('Failed to toggle user status', err);
        alert('Failed to update user status. Please try again.');
      }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
  }
}
