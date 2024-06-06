-- the first 3 refer to the same company
INSERT INTO reports (
    id,
    company_id,
    report_date,
    total_revenue,
    net_profit,
    is_deleted
) VALUES
      (
          '123e4567-e89b-12d3-a456-426614174000',
          '123e4567-e89b-12d3-a456-426614174001',
          '2024-06-06T12:34:56',
          12345.67,
          6789.10,
          FALSE
      ),
      (
          '223e4567-e89b-12d3-a456-426614174000',
          '123e4567-e89b-12d3-a456-426614174001',
          '2024-06-07T12:34:56',
          22345.67,
          7789.10,
          FALSE
      ),
      (
          '323e4567-e89b-12d3-a456-426614174000',
          '123e4567-e89b-12d3-a456-426614174001',
          '2024-06-08T12:34:56',
          32345.67,
          8789.10,
          FALSE
      ),
      (
          '423e4567-e89b-12d3-a456-426614174000',
          '223e4567-e89b-12d3-a456-426614174002',
          '2024-06-09T12:34:56',
          42345.67,
          9789.10,
          FALSE
      ),
      (
          '523e4567-e89b-12d3-a456-426614174000',
          '323e4567-e89b-12d3-a456-426614174003',
          '2024-06-10T12:34:56',
          52345.67,
          10789.10,
          FALSE
      );
