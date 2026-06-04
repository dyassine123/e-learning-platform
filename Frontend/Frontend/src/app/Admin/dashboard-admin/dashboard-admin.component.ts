import { Component, AfterViewInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { UserService } from '../../services/user.service';
import { CourseService } from '../../services/course.service';
import { CategoryService } from '../../services/category.service';
import { forkJoin } from 'rxjs';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard-admin',
  templateUrl: './dashboard-admin.component.html',
  styleUrls: ['./dashboard-admin.component.css']
})
export class DashboardAdminComponent implements AfterViewInit, OnDestroy {
  @ViewChild('barChart') barRef!: ElementRef;
  @ViewChild('doughnutChart') doughnutRef!: ElementRef;
  @ViewChild('pieChart') pieRef!: ElementRef;
  @ViewChild('lineChart') lineRef!: ElementRef;

  private charts: Chart[] = [];
  loading = true;

  // Stats
  totalUsers = 0;
  studentCount = 0;
  teacherCount = 0;
  courseCount = 0;

  constructor(
    private userService: UserService,
    private courseService: CourseService,
    private categoryService: CategoryService
  ) {}

  ngAfterViewInit(): void {
    this.fetchDataAndInitCharts();
  }

  ngOnDestroy(): void {
    this.charts.forEach(c => c.destroy());
  }

  private fetchDataAndInitCharts() {
    this.loading = true;
    
    forkJoin({
      users: this.userService.list(),
      publishedCourses: this.courseService.listPublished(undefined, 0, 1000),
      pendingCourses: this.courseService.listPending(0, 1000),
      categories: this.categoryService.list(0, 1000)
    }).subscribe({
      next: (data) => {
        const users = data.users;
        const publishedCourses = data.publishedCourses.content;
        const pendingCourses = data.pendingCourses.content;
        const categories = data.categories.content;

        // Combine all courses for analysis
        const allCourses = [...publishedCourses, ...pendingCourses];

        this.totalUsers = users.length;
        this.studentCount = users.filter(u => u.role === 'STUDENT').length;
        this.teacherCount = users.filter(u => u.role === 'TEACHER').length;
        this.courseCount = allCourses.length;

        // Data processing for charts
        const categoryCourseCounts = categories.map(cat => {
          return allCourses.filter(c => c.categoryName === cat.name).length;
        });

        this.initAllCharts(users, allCourses, categories, categoryCourseCounts);
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to fetch dashboard data', err);
        this.loading = false;
        // Fallback to empty/mock data if needed
      }
    });
  }

  private initAllCharts(users: any[], courses: any[], categories: any[], categoryCounts: number[]) {
    const publishedCount = courses.filter(c => c.status === 'PUBLISHED').length;
    const pendingCount = courses.filter(c => c.status === 'PENDING_APPROVAL').length;
    
    this.createBarChart(publishedCount, pendingCount);
    this.createDoughnutChart(this.studentCount, this.teacherCount);
    this.createPieChart(categories.map(c => c.name), categoryCounts);
    
    // Process User Growth Data
    const growthData = this.processUserGrowth(users);
    this.createLineChart(growthData.labels, growthData.values);
    this.loading = false;
  }

  private processUserGrowth(users: any[]) {
    // Group users by month
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];
    const currentYear = new Date().getFullYear();
    const countsByMonth = new Array(12).fill(0);

    users.forEach(u => {
      const date = new Date(u.createdAt);
      if (date.getFullYear() === currentYear) {
        countsByMonth[date.getMonth()]++;
      }
    });

    // Calculate cumulative sum
    const labels: string[] = [];
    const values: number[] = [];
    let cumulative = 0;
    const currentMonth = new Date().getMonth();

    for (let i = 0; i <= currentMonth; i++) {
      cumulative += countsByMonth[i];
      labels.push(months[i]);
      values.push(cumulative);
    }

    return { labels, values };
  }

  private createBarChart(published: number, pending: number) {
    this.charts[0] = new Chart(this.barRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Publiés', 'En Attente'],
        datasets: [{
          label: 'Statut du Cours',
          data: [published, pending],
          backgroundColor: ['#10b981', '#f59e0b'],
          borderRadius: 12,
          hoverBackgroundColor: ['#059669', '#d97706']
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { 
            beginAtZero: true, 
            grid: { display: false }, 
            ticks: { stepSize: 1, font: { weight: 'bold' as const } } 
          },
          x: { 
            grid: { display: false }, 
            ticks: { font: { weight: 'bold' as const } } 
          }
        }
      }
    });
  }

  private createDoughnutChart(students: number, teachers: number) {
    this.charts[1] = new Chart(this.doughnutRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Étudiants', 'Enseignants'],
        datasets: [{
          data: [students, teachers],
          backgroundColor: ['#3b82f6', '#a855f7'],
          hoverOffset: 20,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '75%',
        plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, font: { weight: 'bold' as const } } } }
      }
    });
  }

  private createPieChart(labels: string[], data: number[]) {
    this.charts[2] = new Chart(this.pieRef.nativeElement, {
      type: 'pie',
      data: {
        labels: labels.length > 0 ? labels : ['Aucune Donnée'],
        datasets: [{
          data: data.length > 0 ? data : [1],
          backgroundColor: ['#3b82f6', '#a855f7', '#f97316', '#10b981', '#f43f5e', '#6366f1'],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'right', labels: { boxWidth: 12, font: { size: 10, weight: 'bold' as const } } } }
      }
    });
  }

  private createLineChart(labels: string[], values: number[]) {
    this.charts[3] = new Chart(this.lineRef.nativeElement, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Utilisateurs Totaux',
          data: values,
          borderColor: '#3b82f6',
          borderWidth: 4,
          tension: 0.4,
          fill: true,
          backgroundColor: 'rgba(59, 130, 246, 0.05)',
          pointBackgroundColor: '#ffffff',
          pointBorderColor: '#3b82f6',
          pointBorderWidth: 2,
          pointRadius: 6,
          pointHoverRadius: 8
        }]
      },
      options: this.getBasicOptions()
    });
  }

  private getBasicOptions(): any {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { display: false }, ticks: { font: { weight: 'bold' as const } } },
        x: { grid: { display: false }, ticks: { font: { weight: 'bold' as const } } }
      }
    };
  }
}
