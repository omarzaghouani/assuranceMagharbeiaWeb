export interface Partner {
    id?: number; // Optional because new partners may not have an ID until saved
    name: string; // Name of the partner
    type: string; // Type of partner (e.g., Technology, Healthcare, etc.)
    email: string; // Email address of the partner
    phone: string; // Phone number of the partner
  }
  