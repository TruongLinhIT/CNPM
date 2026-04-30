const DEFAULT_BASE_URL = 'http://localhost:3000/api';

const baseUrl = process.env.BASE_URL || DEFAULT_BASE_URL;
const users = [
  {
    username: 'manager1',
    password: '123456',
    role: 'Manager',
    full_name: 'Manager One'
  },
  {
    username: 'waitstaff1',
    password: '123456',
    role: 'Waitstaff',
    full_name: 'Waitstaff One'
  },
  {
    username: 'kitchen1',
    password: '123456',
    role: 'Kitchen',
    full_name: 'Kitchen One'
  }
];

async function request(method, path, token, body) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${baseUrl}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  let payload = null;
  try {
    payload = await res.json();
  } catch (error) {
    payload = null;
  }

  if (!res.ok) {
    const err = new Error(`Request failed: ${method} ${path} (${res.status})`);
    err.status = res.status;
    err.payload = payload;
    throw err;
  }

  return payload;
}

async function registerIfNeeded(user) {
  try {
    await request('POST', '/auth/register', null, user);
  } catch (error) {
    if (error.status !== 409) {
      throw error;
    }
  }
}

async function login(user) {
  const payload = await request('POST', '/auth/login', null, {
    username: user.username,
    password: user.password
  });
  return payload.data.token;
}

async function main() {
  console.log(`Base URL: ${baseUrl}`);

  for (const user of users) {
    await registerIfNeeded(user);
  }

  const managerToken = await login(users[0]);
  const waitstaffToken = await login(users[1]);

  const category = await request('POST', '/categories', managerToken, {
    name: 'Test Category',
    description: 'Smoke test'
  });

  const menuItem = await request('POST', '/menu-items', managerToken, {
    category_id: category.data.category_id,
    name: 'Test Item',
    description: 'Smoke test item',
    price: 10000,
    is_available: true
  });

  const table = await request('POST', '/tables', managerToken, {
    table_number: Math.floor(Math.random() * 10000),
    capacity: 2,
    status: 'Available'
  });

  await request('GET', '/categories', managerToken);
  await request('GET', `/categories/${category.data.category_id}`, managerToken);
  await request('GET', '/menu-items', managerToken);
  await request('GET', `/menu-items/${menuItem.data.item_id}`, managerToken);
  await request('GET', '/tables', managerToken);
  await request('GET', `/tables/${table.data.table_id}`, managerToken);

  const order = await request('POST', '/orders', waitstaffToken, {
    table_id: table.data.table_id,
    items: [{ item_id: menuItem.data.item_id, quantity: 1 }],
    discount: 0
  });

  await request('GET', '/orders', managerToken);
  const orderDetailResponse = await request(
    'GET',
    `/orders/${order.data.order_id}`,
    managerToken
  );

  const details =
    orderDetailResponse.data.OrderDetails ||
    orderDetailResponse.data.order_details ||
    orderDetailResponse.data.orderDetails ||
    [];

  const orderDetailId = details.length > 0 ? details[0].order_detail_id : null;

  await request(
    'PATCH',
    `/orders/${order.data.order_id}/status`,
    waitstaffToken,
    { status: 'Preparing' }
  );

  if (orderDetailId) {
    await request(
      'PATCH',
      `/orders/items/${orderDetailId}/status`,
      waitstaffToken,
      { status: 'Ready' }
    );
  }

  await request('POST', '/payments', waitstaffToken, {
    order_id: order.data.order_id,
    amount_paid: order.data.total_amount || 10000,
    payment_method: 'Cash'
  });

  await request('GET', '/payments', managerToken);
  await request('GET', '/reports/revenue', managerToken);

  console.log('Smoke test OK');
}

main().catch((error) => {
  console.error('Smoke test failed:', error.message);
  if (error.payload) {
    console.error(JSON.stringify(error.payload, null, 2));
  }
  process.exit(1);
});
