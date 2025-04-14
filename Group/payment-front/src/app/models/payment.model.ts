export interface Payment {
    id?: number; // Marking it optional
    amount: number;
    method: string;
    status: string;
    date: string;
  }
  