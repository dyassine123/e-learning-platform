export interface TeacherNavItem {
  label: string;
  route: string;
  iconPath: string;
  section: 'main' | 'tools';
  enabled: boolean;
}

export const TEACHER_NAV_ITEMS: TeacherNavItem[] = [
  {
    label: 'Tableau de bord',
    route: '/teacher/dashboard',
    iconPath: 'M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z',
    section: 'main',
    enabled: true
  },
  {
    label: 'Mes Cours',
    route: '/teacher/courses',
    iconPath: 'M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10',
    section: 'main',
    enabled: true
  },
  {
    label: 'Créer un Cours',
    route: '/teacher/create-course',
    iconPath: 'M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z',
    section: 'main',
    enabled: true
  },
  {
    label: 'Constructeur de Quiz',
    route: '/teacher/quiz-builder',
    iconPath: 'M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    section: 'tools',
    enabled: true
  },
  {
    label: 'Progression des Étudiants',
    route: '/teacher/student-progress',
    iconPath: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z',
    section: 'tools',
    enabled: false
  }
];
