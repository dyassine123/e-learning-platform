export interface StudentNavItem {
  label: string;
  route: string;
  iconPath: string;
  section: 'main' | 'account';
  enabled: boolean;
}

export const STUDENT_NAV_ITEMS: StudentNavItem[] = [
  {
    label: 'Tableau de bord',
    route: '/student/dashboard',
    iconPath: 'M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z',
    section: 'main',
    enabled: true
  },
  {
    label: 'Mes Cours',
    route: '/student/courses',
    iconPath: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5s3.332.477 4.5 1.253',
    section: 'main',
    enabled: true
  },
  {
    label: 'Explorer',
    route: '/student/explore',
    iconPath: 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z',
    section: 'main',
    enabled: true
  },
  {
    label: 'Quiz',
    route: '/student/quiz-player',
    iconPath: 'M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    section: 'main',
    enabled: true
  },
];
